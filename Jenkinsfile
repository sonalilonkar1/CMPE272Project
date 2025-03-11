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
                // Ensure Maven has access to a writable repository
                sh 'mkdir -p /root/.m2/repository && chmod -R 777 /root/.m2/repository'
                
                // Run Maven build with explicit repo location
                sh 'mvn -Dmaven.repo.local=/root/.m2/repository clean package'
            }
        }
        stage('List Files') {
            steps {
                sh 'ls -lah'
            }
        }
    }
}

