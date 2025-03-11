pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-17-alpine'
            args '-v $HOME/.m2:/root/.m2' // Caching Maven dependencies
        }
    }
    environment {
        // Define environment variables
        MAVEN_REPO = '/tmp/.m2/repository'  // Maven repository location
        PROJECT_DIR = 'backend'             // Directory containing the pom.xml
	MYSQL_HOST = "mysql"  // The MySQL service in Docker Compose
        MYSQL_PORT = "3306"
        MYSQL_USER = "sonalilonkar"
        MYSQL_PASSWORD = "password"
        MYSQL_DATABASE = "charity"
    }
    stages {
        stage('Setup') {
            steps {
                sh 'mvn --version'
            }
        }
        stage('Build Project') {
            steps {
                // Use the environment variable for the Maven repo path and project dir
                dir("${env.PROJECT_DIR}") {
                    // Create a writable directory for Maven repository
                    sh "mkdir -p ${env.MAVEN_REPO} && chmod -R 777 ${env.MAVEN_REPO}"

                    // Run Maven build with the custom repository location
                    sh "mvn -Dmaven.repo.local=${env.MAVEN_REPO} clean package"
                }
            }
        }
        stage('List Files') {
            steps {
                sh 'ls -lah'
            }
        }
    }
}
