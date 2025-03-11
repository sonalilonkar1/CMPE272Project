pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-21-alpine'
            args '-v $HOME/.m2:/root/.m2' // Caching Maven dependencies
        }
    }
    stages {
        stage('Setup') {
            steps {
                sh 'mvn --version'
            }
        }
        stage('Build Project') {
            steps {
                // Navigate to the backend directory where pom.xml is located
                dir('backend') {
                    // Create a writable directory for Maven repository
                    sh 'mkdir -p /tmp/.m2/repository && chmod -R 777 /tmp/.m2/repository'

                    // Run Maven build with the custom repository location
                    sh 'mvn -Dmaven.repo.local=/tmp/.m2/repository clean package'
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
