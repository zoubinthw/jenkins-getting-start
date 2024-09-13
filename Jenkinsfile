pipeline {
    agent {
        label 'maven-jdk17'
    }

    environment {
        GIT_REPO = 'https://github.com/zoubinthw/jenkins-getting-start.git'  // Replace with your GitHub repo
        BRANCH = 'main'  // Branch to build
//         DOCKER_REGISTRY = 'your-docker-repo'  // Replace with your Docker repository (e.g., yourusername/repository)
//         DOCKER_CREDENTIALS_ID = 'docker-credentials'  // Jenkins ID for Docker credentials
        GIT_CREDENTIALS_ID = 'gittoken'  // Jenkins ID for GitHub credentials
//         KUBERNETES_DEPLOYMENT_YAML = 'k8s/deployment.yaml'  // Path to your Kubernetes deployment YAML in your repository
//         DOCKER_IMAGE_TAG = "your-docker-repo:${env.BUILD_NUMBER}"  // Tag for Docker image
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

        stage('Build with Maven') {
            steps {
                container('maven') {
                    // Clean and package the Maven project
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

//         stage('Build Docker Image') {
//             steps {
//                 container('docker') {
//                     // Build Docker image using Dockerfile in the project
//                     sh "docker build -t ${DOCKER_IMAGE_TAG} ."
//                 }
//             }
//         }
//
//         stage('Push Docker Image') {
//             steps {
//                 container('docker') {
//                     // Login to Docker registry and push the image
//                     withDockerRegistry([ credentialsId: "${DOCKER_CREDENTIALS_ID}", url: '' ]) {
//                         sh "docker push ${DOCKER_IMAGE_TAG}"
//                     }
//                 }
//             }
//         }
//
//         stage('Deploy to Kubernetes') {
//             steps {
//                 container('maven') {
//                     // Use kubectl to deploy the app in Kubernetes
//                     sh """
//                     kubectl apply -f ${KUBERNETES_DEPLOYMENT_YAML}
//                     kubectl set image deployment/your-deployment-name your-container-name=${DOCKER_IMAGE_TAG}
//                     """
//                 }
//             }
//         }
    }

//     post {
//         always {
//             container('maven') {
//                 // Cleanup Docker images to save space
//                 sh 'docker system prune -af'
//             }
//         }
//     }
}
