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
            
        }
        stage('Getting key') {
            steps {
                    echo 'Getting key ....' 
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
                                        println issueKeys
                                        println issueKeys.size()
                                        if (issueKeys[0]=='') { jira = false ; echo 'jira = false'} else { jira = true ; echo 'jira = true'}
                                    }
                                }
                }
        }
        stage('To Jira') {
            when {
                expression { jira }
            }
            steps {
                echo 'Deploying....'
                jiraSendDeploymentInfo site: 'chauphan.atlassian.net',
                environmentId: "${ENV}",
                               environmentName: "${ENV}",
                               environmentType: "${environmentType}",
                                issueKeys: issueKeys
            }
            
        }
    }
}