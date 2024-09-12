pipeline {
    agent {
        label 'maven-jdk17'
    }

    stages {
        stage('打印mvn版本') {
            steps {
                container('maven') {
                    sh 'mvn -v'
                }
            }
        }
    }
}