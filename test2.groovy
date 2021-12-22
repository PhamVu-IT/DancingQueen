issueKeys = sh(returnStdout: true,script: 'detect-jira-issue-keys.py').trim().split('\n') as List
println issueKeys
