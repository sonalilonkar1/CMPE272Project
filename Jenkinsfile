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
                sh 'mvn clean package'
            }
        }
        stage('List Files') {
            steps {
                sh 'ls -lah'
            }
        }
    }
}

