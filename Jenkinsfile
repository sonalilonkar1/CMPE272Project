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
    }
}
