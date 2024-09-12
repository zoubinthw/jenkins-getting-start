pipeline {
    agent {
        label 'maven-jdk17'
    }
    triggers {
        githubPush() // This is for GitHub push trigger
    }
    stages {
        stage('test') {
            steps {
                container('maven') {
                    sh 'mvn -v'
                }
            }
        }
        stage('Check out') {
            steps {
                checkout scm
                sh '拉取代码成功'
            }
        }
    }
}