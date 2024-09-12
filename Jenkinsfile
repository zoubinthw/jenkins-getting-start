pipeline {
    agent {
        label 'maven-jdk17'
    }
    triggers {
        githubPush() // This is for GitHub push trigger
    }
    stages {
        stage('mvn version') {
            steps {
                container('maven') {
                    sh 'mvn -v'
                }
            }
        }
        stage('Check out') {
            steps {
                checkout scm
            }
        }
        stage('Build') {
            steps {
                container('maven') {
                    sh 'mvn clean install'
                }
            }
        }
    }
}