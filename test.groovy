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
                echo "Getting issueKeys"
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
            }
            steps {
                when {
                // Only say sending to Jira if a "issueKey" is not null
                    expression { issueKeys != '' }
                }
                echo 'Testing condition. Sending to JIRA cloud'
                                            
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