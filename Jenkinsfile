pipeline {
    agent any

    environment {
        VPS_USER = "jgrando"
        VPS_IP = "72.61.47.148"
        SSH_CREDENTIALS_ID = "vps-ssh-key"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Determine Environment') {
            steps {
                script {
                    String branch = env.BRANCH_NAME ?: env.GIT_BRANCH ?: ""
                    
                    if (branch.contains('/')) {
                        branch = branch.split('/')[-1]
                    }

                    echo "Detected branch: ${branch}"

                    if (branch == 'master') {
                        env.DEPLOY_ENV = 'prod'
                        env.SERVICE_NAME = 'americobarber-prod'
                    } else if (branch == 'hml' || branch == 'develop' || branch == 'staging') {
                        env.DEPLOY_ENV = 'hml'
                        env.SERVICE_NAME = 'americobarber-hml'
                    } else {
                        error "Branch '${branch}' is not configured for deployment."
                    }
                }
            }
        }

        stage('Deploy to VPS') {
            steps {
                sshagent([SSH_CREDENTIALS_ID]) {
                    sh """
                        ssh -o StrictHostKeyChecking=no ${VPS_USER}@${VPS_IP} "mkdir -p /opt/americobarber/${env.DEPLOY_ENV}"
                        scp -o StrictHostKeyChecking=no target/*.jar ${VPS_USER}@${VPS_IP}:/opt/americobarber/${env.DEPLOY_ENV}/app.jar
                        
                        ssh -o StrictHostKeyChecking=no ${VPS_USER}@${VPS_IP} "sudo systemctl restart ${env.SERVICE_NAME}"
                    """
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
    }
}
