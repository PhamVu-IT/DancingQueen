def task = "python3 detect-jira-issue-keys.py".execute()
task.waitFor()
println task.text
