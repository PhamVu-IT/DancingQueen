pipeline {
    agent any
    
    stages {
        stage('Git checkout') {
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/ptchau2003/DancingQueen.git']]])
            }
        }
        stage('Build') {
            steps {
                echo 'Building..'
            }
        }
        stage('Test') {
            steps {
                echo 'Testing..'
            }
        }
        stage('Deploy') {
            
            steps {
                echo 'Deploying....'
            }

            steps {
                when {
                // Only say hello if a "greeting" is requested
                    expression { issueKeys != '' }
                }
                echo 'Testing condition'
                            dir('.') {
                                    script {
                                        environmentType = "${ENV}"
                                        if (environmentType == "preprod") {
                                            environmentType = "unmapped"
                                        }
                    
                                        issueKeys = sh(
                                            returnStdout: true,
                                            script: './detect-jira-issue-keys.py'
                                        ).trim().split('\n') as List
                                    }
                                }
                   
                 
                            jiraSendDeploymentInfo site: 'chauphan.atlassian.net',
                                environmentId: "${ENV}",
                                environmentName: "${ENV}",
                                environmentType: "${environmentType}",
                                issueKeys: issueKeys

            }
            post {
                always {
                    
                }
                

                cleanup {
                    cleanWs()
                }
            }
        }
    }
}