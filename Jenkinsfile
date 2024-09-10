pipeline {
    agent {
        label 'mvn-agent'
    }

    stages {
        stage('打印mvn版本') {
            steps {
                sh 'mvn -v'
            }
        }
    }
}