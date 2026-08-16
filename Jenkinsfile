pipeline {
    agent any

    tools {
        maven 'Maven 3.9.5'
        jdk 'JDK 17'
    }

    environment {
        MAVEN_OPTS = '-Xmx1024m'
        APP_NAME = 'demo'
        APP_VERSION = ''
        PROJECT_SUB_DIR = '.'
        WSL_HOST = '192.168.178.238'
        WSL_USER = 'root'
        WSL_CRED = 'wsl-root-ssh'
        DEPLOY_PATH = '/opt/deployments'
        JAR_FILE = 'hello-aws-demo.jar'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timestamps()
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                echo '🔍 Checking out code...'
                git branch: 'main',
                     url: 'git@github.com:Jemmy-Hong/aws-springboot-demo.git',
                     credentialsId: 'github-ssh'
                script {
                    env.GIT_COMMIT_MSG = bat(
                        script: '@echo off && git log -1 --pretty=%%B',
                        returnStdout: true
                    ).trim()
                    env.GIT_COMMIT_AUTHOR = bat(
                        script: '@echo off && git log -1 --pretty=%%an',
                        returnStdout: true
                    ).trim()
                }
                echo "Commit: ${env.GIT_COMMIT_MSG} by ${env.GIT_COMMIT_AUTHOR}"
            }
        }

        stage('Build') {
            steps {
                echo '🔨 Building application...'
                bat '''
                    mvn clean package -DskipTests
                '''
            }
            post {
                success {
                    echo '✅ Build completed successfully!'
                    archiveArtifacts artifacts: "target/*.jar", fingerprint: true
                }
                failure {
                    echo '❌ Build failed!'
                }
            }
        }

        stage('Test') {
            steps {
                echo '🧪 Running tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: "**/target/surefire-reports/*.xml"
                }
                success {
                    echo '✅ All tests passed!'
                }
                failure {
                    echo '❌ Tests failed!'
                }
            }
        }

        stage('Code Quality') {
            steps {
                echo '📊 Analyzing code quality...'
                bat 'mvn verify'
            }
        }

        stage('Deploy') {
            steps {
                echo '🚀 Deploying application'
                script {
                    withCredentials([sshUserPrivateKey(credentialsId: env.WSL_CRED, keyFileVariable: 'TEMP_SSH_KEY')]) {
                        bat '''
icacls "%TEMP_SSH_KEY%" /inheritance:r
icacls "%TEMP_SSH_KEY%" /grant "NT AUTHORITY\\SYSTEM:F"

scp -i "%TEMP_SSH_KEY%" -o StrictHostKeyChecking=no target\\%JAR_FILE% %WSL_USER%@%WSL_HOST%:%DEPLOY_PATH%/

ssh -i "%TEMP_SSH_KEY%" -o StrictHostKeyChecking=no %WSL_USER%@%WSL_HOST% "cd %DEPLOY_PATH% && ./deploy.sh"
'''
                    }
                }
            }
            post {
                success {
                    echo '✅ Deployment completed successfully!'
                }
                failure {
                    echo '❌ Deployment failed!'
                }
            }
        }
    }

    post {
        success {
            echo '🎉 Pipeline completed successfully!'
        }
        failure {
            echo '💥 Pipeline failed!'
        }
        unstable {
            echo '⚠️ Pipeline is unstable!'
        }
    }
}