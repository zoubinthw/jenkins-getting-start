pipeline {
    agent {
        kubernetes {
            // Define the Kubernetes pod with containers for Maven and Docker
            yaml '''
            apiVersion: v1
            kind: Pod
            spec:
              containers:
              - name: jnlp
                image: jenkins/inbound-agent:latest
                args: ['\$(JENKINS_SECRET)', '\$(JENKINS_NAME)']
              - name: maven
                image: maven:3.8.5-openjdk-17
                command:
                - cat
                tty: true
              - name: kubectl
                image: bitnami/kubectl:latest
                command:
                - cat
                tty: true
                securityContext:
                  runAsUser: 1000
              - name: aws-cli
                image: amazon/aws-cli
                command:
                - cat
                tty: true
                volumeMounts:
                - name: docker-socket
                  mountPath: /var/run/docker.sock
              - name: dind
                image: docker:19.03.12-dind
                securityContext:
                  privileged: true
                env:
                - name: DOCKER_TLS_CERTDIR
                  value: ""
                volumeMounts:
                - name: docker-graph-storage
                  mountPath: /var/lib/docker
              volumes:
              - name: docker-socket
                hostPath:
                  path: /var/run/docker.sock
              - name: docker-graph-storage
                emptyDir: {}
            '''
        }
    }

    environment {
        GIT_REPO = 'https://github.com/zoubinthw/jenkins-getting-start.git'  // Replace with your GitHub repo
        DOCKER_IMAGE = "jenkins-demo-app"  // Set your Docker image name here
        GIT_CREDENTIALS_ID = 'gittoken'  // Jenkins ID for GitHub credentials, 这个其实拿到了
        KUBECONFIG_CREDENTIALS = 'kubeconfig-creds'  // Jenkins credentials ID for Kubernetes config
        KUBE_NAMESPACE = 'dev'  // Kubernetes namespace for deployment
        AWS_CREDENTIALS = 'aws-ecr-creds'  // docker镜像的获取和存储放到自己的aws ecr仓库中
        AWS_REGION = 'ap-east-1'  // e.g., 'us-west-1'
        ECR_REPOSITORY = 'binzou/jenkins-demo'  // Your ECR repository name
        AWS_ACCOUNT_ID = '471112990918'  // AWS Account ID
    }

    parameters {
        string(name: 'PROJECT_VERSION', defaultValue: '1.0', description: '项目版本')
        string(name: 'PROJECT_NAME', defaultValue: 'demo', description: '项目名称')
        choice(name: 'BRANCH', choices: ['main', 'dev'], description: '部署的分支')
        choice(name: 'PROFILE', choices: ['dev', 'local'], description: '使用的配置文件')
    }

    stages {
        stage('拉取代码') {
            steps {
                container('maven') {
                    echo "拉取分支: ${params.BRANCH}"
                    // Checkout the repository from GitHub
                    checkout([
                        $class: 'GitSCM',
                        branches: [[name: "*/${params.BRANCH}"]],
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
                    sh "echo 开始maven build, 使用的配置为: ${params.PROFILE}"
                    sh "mvn clean package -P ${params.PROFILE}"
                }
            }
        }

        stage('Install Docker CLI in awscli container') {
            steps {
                container('aws-cli') {
                    script {
                        sh '''
                            yum update -y
                            yum install -y docker
                        '''
                    }
                }
            }
        }

        stage('Docker image build') {
            steps {
                container('aws-cli') {
                    // jar包在: ./target/demo-0.0.1-SNAPSHOT.jar
                    sh 'echo 开始制作镜像, 镜像名称为: ${DOCKER_IMAGE}:${BUILD_NUMBER}'
                    sh 'docker build -t ${DOCKER_IMAGE}:${BUILD_NUMBER} .'
                }
            }
        }

        stage('获取AWS ECR认证信息') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: "${AWS_CREDENTIALS}"
                ]]) {
                    container('aws-cli') {
                        script {
                            sh '''
                            aws ecr get-login-password --region ap-east-1 | docker login --username AWS --password-stdin 471112990918.dkr.ecr.ap-east-1.amazonaws.com
                            '''
                        }
                    }
                }
            }
        }

        stage('推送Docker镜像') {
            steps {
                container('aws-cli') {
                    script {
                        sh '''
                        echo 推送镜像到ECR [${BUILD_NUMBER} , latest]
                        # Tag the image
                        docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}
                        # Push the image to ECR
                        docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:${BUILD_NUMBER}

                        # Tag the image as 'latest'
                        docker tag ${DOCKER_IMAGE}:${BUILD_NUMBER} ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:latest
                        docker push ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_REPOSITORY}:latest
                        '''
                    }
                }
            }
        }

        stage('部署到Kubernetes集群') {
            steps {
                withCredentials([file(credentialsId: 'kubeconfig-creds', variable: 'KUBECONFIG')]) {
                    container('kubectl') {
                        sh """
                        echo 当前部署版本为: ${BUILD_NUMBER}
                        sed -i -e 's/place_holder_namespace/${KUBE_NAMESPACE}/g' \
                               -e 's/place_holder_account_id/${AWS_ACCOUNT_ID}/g' \
                               -e 's/place_holder_region/${AWS_REGION}/g' \
                               -e 's#place_holder_repository#${ECR_REPOSITORY}#g' \
                               -e 's/place_holder_build_no/${BUILD_NUMBER}/g' \
                            jenkins-demo-deployment.yaml
                        kubectl apply -f jenkins-demo-deployment.yaml
                        kubectl --kubeconfig=$KUBECONFIG rollout status deployment/jenkins-demo --namespace=dev
                        """
                    }
                }
            }
        }
    }
}
