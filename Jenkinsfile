pipeline {
    agent {
        kubernetes {
            // Define the Kubernetes pod with containers for Maven and Docker
            yaml '''
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: maven
                image: maven:3.8.5-openjdk-17
                command:
                - cat
                tty: true
              - name: docker
                image: docker:20.10.7
                command:
                - cat
                tty: true
            '''
        }
    }

    environment {
        GIT_REPO = 'https://github.com/zoubinthw/jenkins-getting-start.git'  // Replace with your GitHub repo
        BRANCH = 'main'  // Branch to build
//         DOCKER_REGISTRY = 'your-docker-repo'  // Replace with your Docker repository (e.g., yourusername/repository)
//         DOCKER_CREDENTIALS_ID = 'docker-credentials'  // Jenkins ID for Docker credentials
        GIT_CREDENTIALS_ID = credentials('gittoken')  // Jenkins ID for GitHub credentials, 这个其实拿到了
//         KUBERNETES_DEPLOYMENT_YAML = 'k8s/deployment.yaml'  // Path to your Kubernetes deployment YAML in your repository
//         DOCKER_IMAGE_TAG = "your-docker-repo:${env.BUILD_NUMBER}"  // Tag for Docker image
        GIT_TOKEN = credentials('gittoken')
    }

    stages {
        stage('Clone Repository') {
            steps {
                container('maven') {
                    // Checkout the repository from GitHub
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${BRANCH}"]],
                        userRemoteConfigs: [[
                            url: "${GIT_REPO}",
                            credentialsId: "${GIT_CREDENTIALS_ID}"
                        ]]
                    ])
                }
            }
        }
        stage('Test02') {
            steps {
                script {
                    echo "Git token is: ${env.gittoken}"
                }
            }
        }

        stage('Build with Maven') {
            steps {
                container('maven') {
                    // Clean and package the Maven project
                    sh 'mvn -v'
                    sh 'ls -a' // 看看现在的文件结构
                }
            }
        }
    }
}
