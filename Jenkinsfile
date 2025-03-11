pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-17-alpine'
            args '-v $HOME/.m2:/root/.m2' // Caching Maven dependencies
        }
    }

    environment {
        MAVEN_REPO = '/tmp/.m2/repository'  // Maven repository location
        PROJECT_DIR = 'backend'             // Directory containing the pom.xml
        DEPLOY_SERVER = 'your.server.com'   // Change this to your server's IP or hostname
        DEPLOY_DIR = '/path/to/deploy/directory' // The directory on the remote server to deploy to
        SSH_KEY_PATH = '/path/to/your/private/key' // Path to your SSH private key (use Jenkins credentials store if possible)
        DEPLOY_SCRIPT_PATH = '/path/to/deploy.sh'  // Path to your deployment script on the remote server
    }

    stages {
        stage('Setup') {
            steps {
                sh 'mvn --version'
            }
        }

        stage('Build Project') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    sh "mkdir -p ${env.MAVEN_REPO} && chmod -R 777 ${env.MAVEN_REPO}"
                    // Use -Dspring.profiles.active=test to activate the test profile
                    sh "mvn -Dmaven.repo.local=${env.MAVEN_REPO} -Dspring.profiles.active=test clean package"
                }
            }
        }

        stage('Test') {
            steps {
                dir("${env.PROJECT_DIR}") {
                    // Running tests with the active test profile
                    sh "mvn -Dmaven.repo.local=${env.MAVEN_REPO} -Dspring.profiles.active=test test"
                }
            }
        }

        stage('List Files') {
            steps {
                sh 'ls -lah'
            }
        }

        stage('Deploy') {
            steps {
                script {
                    // SCP the jar file to the remote server
                    sh """
                        echo 'Deploying to server...'
                        scp -i ${SSH_KEY_PATH} target/*.jar user@${DEPLOY_SERVER}:${DEPLOY_DIR}
                    """
                    // SSH into the server and run deployment script
                    sh """
                        echo 'Running deployment script on server...'
                        ssh -i ${SSH_KEY_PATH} user@${DEPLOY_SERVER} 'bash -s' < ${DEPLOY_SCRIPT_PATH}
                    """
                }
            }
        }
    }

    post {
        success {
            // Send success email
            emailext(
                subject: "Build SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "The build was successful. Check the console output for more details.\n\nJob URL: ${env.BUILD_URL}",
                to: "sonalilonkar0301@gmail.com"  
            )
        }

        failure {
            // Send failure email
            emailext(
                subject: "Build FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "The build has failed. Check the console output for details.\n\nJob URL: ${env.BUILD_URL}",
                to: "sonalilonkar0301@gmail.com"  
            )
        }

        always {
            // Cleanup actions, like stopping Docker containers, removing temp files, etc.
            echo 'Cleaning up resources...'
            // Example: Cleanup Docker resources
            sh 'docker system prune -f' // Removes unused Docker containers and images
        }
    }
}
