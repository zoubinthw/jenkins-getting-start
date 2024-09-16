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
              - name: kubectl
                image: bitnami/kubectl
                command:
                - cat
                tty: true
              - name: aws-cli
                image: amazon/aws-cli
                command:
                - cat
                tty: true
              volumes:
              - name: docker-sock
                hostPath:
                  path: /var/run/docker.sock
            '''
        }
    }

    environment {
        GIT_REPO = 'https://github.com/zoubinthw/jenkins-getting-start.git'  // Replace with your GitHub repo
        DOCKER_IMAGE = "jenkins-demo-app"  // Set your Docker image name here
        GIT_CREDENTIALS_ID = credentials('gittoken')  // Jenkins ID for GitHub credentials, 这个其实拿到了
        KUBECONFIG_CREDENTIALS = 'kubeconfig-creds'  // Jenkins credentials ID for Kubernetes config
        KUBE_NAMESPACE = 'dev-jenkins'  // Kubernetes namespace for deployment
        AWS_CREDENTIALS = 'aws-ecr-creds'  // docker镜像的获取和存储放到自己的aws ecr仓库中
        AWS_REGION = 'ap-east-1'  // e.g., 'us-west-1'
        ECR_REPOSITORY = 'binzou/jenkins-demo'  // Your ECR repository name
        AWS_ACCOUNT_ID = '471112990918'  // AWS Account ID
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
//
//         stage('Maven Build') {
//             steps {
//                 container('maven') {
//                     // Clean and package the Maven project
//                     sh 'echo "开始build"'
//                     sh 'mvn clean install -Dmaven.test.skip=true'
//                     sh 'mvn clean package'
//                 }
//             }
//         }
//
//         stage('Docker image build') {
//             steps {
//                 container('docker') {
//                     // jar包在: ./target/demo-0.0.1-SNAPSHOT.jar
//                     sh 'echo "当前目录是: " `pwd`'  // /home/jenkins/agent/workspace/mvn-scm-demo
//                     sh 'echo 镜像名称为: ${DOCKER_IMAGE}:${BUILD_NUMBER}'
//                     sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .'
//                 }
//             }
//         }

        stage('Authenticate with AWS ECR') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: "${AWS_CREDENTIALS}"
                ]]) {
                    container('aws-cli') {
                        script {
//                             sh '''
//                             # Get ECR login command and authenticate Docker with AWS
//                             # $(aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com)
//                             # Example: Fetch an ECR login URL
//                             # def ecrLoginUrl = sh(script: 'aws ecr get-login-password --region ${AWS_REGION}', returnStdout: true).trim()
//                             export PSS=$(aws ecr get-login-password --region ${AWS_REGION})
//                             # Write environment variables to a file
//                             writeFile file: '/tmp/env-vars/env-vars.properties', text: """
//                             ECR_LOGIN_URL=${PSS}
//                             """
//                             '''
                            def ecrLoginUrl = sh(script: 'aws ecr get-login-password --region ${AWS_REGION}', returnStdout: true).trim()
                            writeFile file: 'env-vars.properties', text: "ECR_LOGIN_URL=${ecrLoginUrl}"
                        }
                    }
                }
            }
        }

        stage('docker镜像提交') {
            steps {
                container('docker') {
                    script {
                        // Read the properties file
                        def props = new Properties()
                        def propsFile = new File('env-vars.properties')
                        if (propsFile.exists()) {
                            props.load(new FileInputStream(propsFile))
                        }

                        // Export the variable and use it
                        def ecrLoginUrl = props.getProperty('ECR_LOGIN_URL')
                        withEnv(["ECR_LOGIN_URL=${ecrLoginUrl}"]) {
                            sh 'echo "ECR Login URL: ${ECR_LOGIN_URL}"'
                            // Use ${ECR_LOGIN_URL} in your Docker commands or other scripts
                        }
                    }
                }
            }
        }

        //  这里使用aws来保存镜像
//         stage('Push image to AWS ECR') {
//             steps {
//                 withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
//                                   credentialsId: "${AWS_CREDENTIALS}"
//                 ]]) {
//                     container('docker') {
//                         script {
//                             // Use the ECR plugin to authenticate and push the image
//                             sh '''
//                             # Tag the image
//                             docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}
//
//                             # Push the image to ECR
//                             docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}
//
//                             # Tag the image as 'latest'
//                             docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:latest
//                             docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:latest
//                             '''
//                         }
//                     }
//                 }
//             }
//         }

//         stage('Push Docker Image to AWS ECR') {
//             steps {
//                 container('docker') {
//                     script {
//                         sh '''
//                         # Tag the image
//                         docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}
//
//                         # Push the image to ECR
//                         docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}
//
//                         # Tag the image as 'latest'
//                         docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:latest
//                         docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:latest
//                         '''
//                     }
//                 }
//             }
//         }

//         stage('Push Latest App Image') {
//             steps {
//                 container('docker') {
//                     script {
//                         sh '''
//                         # Tag the previously pushed image as 'latest'
//                         docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${DOCKER_IMAGE}:latest
//
//                         # Push the 'latest' tag to Docker Hub
//                         docker push ${DOCKER_IMAGE}:latest
//                         '''
//                     }
//                 }
//             }
//         }

//         stage('Deploy to Kubernetes') {
//             steps {
//                 withCredentials([file(credentialsId: "${KUBECONFIG_CREDENTIALS}", variable: 'KUBECONFIG')]) {
//                     script {
//                         sh '''
//                         # Set the KUBECONFIG for the kubectl command
//                         export KUBECONFIG=${KUBECONFIG}
//
//                         echo 看看环境中有没有这个变量: $KUBECONFIG
//
//                         # Apply the deployment YAML
//                         kubectl apply -f jenkins-demo-deployment.yaml --namespace=${KUBE_NAMESPACE}
//                         '''
//                     }
//                 }
//             }
//         }
    }
}
