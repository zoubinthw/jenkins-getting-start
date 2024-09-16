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
                image: docker:20.10.8
                command:
                - cat
                tty: true
                volumeMounts:
                - name: docker-sock
                  mountPath: /var/run/docker.sock
              volumes:
              - name: docker-sock
                hostPath:
                  path: /var/run/docker.sock
            '''
        }
    }

    environment {
        GIT_REPO = 'https://github.com/zoubinthw/jenkins-getting-start.git'  // Replace with your GitHub repo
        DOCKER_IMAGE = "binzoooooo/jenkins-demo-app"  // Set your Docker image name here
        REGISTRY_CREDENTIALS = 'dockerAuthInfo'  // Jenkins credential ID for Docker registry
        GIT_CREDENTIALS_ID = credentials('gittoken')  // Jenkins ID for GitHub credentials, 这个其实拿到了
        KUBECONFIG_CREDENTIALS = 'kubeconfig-creds'  // Jenkins credentials ID for Kubernetes config
        KUBE_NAMESPACE = 'dev-jenkins'  // Kubernetes namespace for deployment
    }

    parameters {
        string(name: 'PROJECT_VERSION', defaultValue: '1.0', description: '')
        string(name: 'PROJECT_NAME', defaultValue: 'demo', description: '')
        string(name: 'BRANCH', defaultValue: 'main', description: '')
    }

    stages {
        stage('拉取代码') {
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

        stage('Maven Build') {
            steps {
                container('maven') {
                    // Clean and package the Maven project
                    sh 'echo "开始build"'
                    sh 'mvn clean install -Dmaven.test.skip=true'
                    sh 'mvn clean package'
                }
            }
        }

        stage('Docker image build') {
            steps {
                container('docker') {
                    // jar包在: ./target/demo-0.0.1-SNAPSHOT.jar
                    sh 'echo "当前目录是: " `pwd`'  // /home/jenkins/agent/workspace/mvn-scm-demo
                    sh 'echo 镜像名称为: ${DOCKER_IMAGE}:${BUILD_NUMBER}'
                    sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .'
                }
            }
        }

        stage('Docker Image Push ') {
            steps {
                container('docker') {
                    withCredentials([usernamePassword(credentialsId: "${REGISTRY_CREDENTIALS}", passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        script {
                            sh '''
                            echo "$DOCKER_PASSWORD" | docker login -u "$DOCKER_USERNAME" --password-stdin
                            docker push ${DOCKER_IMAGE}:${BUILD_NUMBER}
                            '''
                        }
                    }
                }
            }
        }

        stage('Push Latest App Image') {
            steps {
                container('docker') {
                    script {
                        sh '''
                        # Tag the previously pushed image as 'latest'
                        docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest

                        # Push the 'latest' tag to Docker Hub
                        docker push ${DOCKER_IMAGE}:latest
                        '''
                    }
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([file(credentialsId: "${KUBECONFIG_CREDENTIALS}", variable: 'KUBECONFIG')]) {
                    script {
                        sh '''
                        # Set the KUBECONFIG for the kubectl command
                        export KUBECONFIG=${KUBECONFIG}

                        echo 看看环境中有没有这个变量: $KUBECONFIG

                        # Apply the deployment YAML
                        kubectl apply -f jenkins-demo-deployment.yaml --namespace=${KUBE_NAMESPACE}
                        '''
                    }
                }
            }
        }
    }
}
