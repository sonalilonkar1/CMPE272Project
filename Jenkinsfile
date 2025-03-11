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
                // Create an alternative writable directory inside the container
                sh 'mkdir -p /tmp/.m2/repository && chmod -R 777 /tmp/.m2/repository'

                // Run Maven build with a different repo location
                sh 'mvn -Dmaven.repo.local=/tmp/.m2/repository clean package'
            }
        }
        stage('List Files') {
            steps {
                sh 'ls -lah'
            }
        }
    }
}
