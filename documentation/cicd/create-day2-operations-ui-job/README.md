# Create a Job for Day 2 Operations UI

The second job which will build the day 2 UI works almost the same as the EasyFranchise UI. So you can follow the description above with the following differences:

## Create a Service Account

You need to create a service account for the **day2-operations** namespace as well, as we did in the previous chapter:

* create **Service Account**
* create a **Cluster Role Biding**

## Configure "Staging" in the CI/CD JOB

* Configure the **General Parameters**: Use day2-ui-001 as Tag
* Configure the **Build**: Use /code/day2-operations/deployment/docker/Dockerfile-day2-ui as path
* Config Acceptance and Release
  * Namespace: day2-operations
  * Deploy Tool: helm3
  * Chart Path: /code/day2-operations/deployment/helmCharts/day2-ui-chart
  * Deployment Name: day2-ui
