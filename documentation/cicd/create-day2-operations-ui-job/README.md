# Create a Job for Day 2 Operations UI

The second job which will build the day 2 UI works almost the same as the EasyFranchise UI. So you can follow the description above with the following differences:

## Create a Service Account

You need to create a service account for the **day2-operations** namespace as well, as we did in the previous chapter:

* Create **Service Account**
* Create a **Cluster Role Binding**

## Configure "Staging" in the Continuous Integration and Delivery Job

* Configure the **General Parameters**: Use day2-ui-001 as a tag
* Configure the **Build**: Use /code/day2-operations/deployment/docker/Dockerfile-day2-ui as path
* Configure the acceptance and release:
  * **Namespace**: day2-operations
  * **Deploy Tool**: helm3
  * **Chart Path**: /code/day2-operations/deployment/helmCharts/day2-ui-chart
  * **Deployment Name**: day2-ui
