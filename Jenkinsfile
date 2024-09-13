pipeline {
    agent {
        label 'maven-jdk17'
    }
//     triggers {
//         githubPush() // This is for GitHub push trigger
//     }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/zoubinthw/jenkins-getting-start.git'
            }
        }
        stage('Build') {
            steps {
                container('docker') {
                    sh 'mvn clean package'
                }
            }
        }
    }
}