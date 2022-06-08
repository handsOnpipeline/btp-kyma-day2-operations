# Deploy the Day2 Application to Kyma (CI/CD)

The following chapters will guide you through the deployment process of the Day 2 Application. There are two options to deploy the application, first we will describe the manual way and second will will show the deployment via script that contains all the manual steps. For the update of the EasyFranchise application we will only use the build script and do not explain the manual steps. If you want to understand more about the EasyFranchise deployment check the [Deployment Chapter of the related mission](https://github.com/SAP-samples/btp-kyma-multitenant-extension/tree/main/documentation/deploy).

## Prerequisites

To execute all the necessary steps for the deployment, you will need the following software available on your machine:

* [Docker](https://docs.docker.com/get-started/#download-and-install-docker)
* [Docker Hub Account](https://hub.docker.com/)
* [HELM](https://helm.sh/docs/intro/install/)
* [Kubernetes CLI](https://kubernetes.io/docs/tasks/tools/#kubectl)
* [Kubernetes OpenID Connect (OIDC) authentication](https://github.com/int128/kubelogin)
* [jq](https://stedolan.github.io/jq/) 
* [uuidgen](https://packages.ubuntu.com/bionic/uuid-runtime)

**NOTE** If you are a Windows User we recommend using a Linux subsystem for the tutorial as our scripts are only available as bash scripts. Furthermore, most of the examples around Kubernetes, for example, are written for Linux/MacOs environments. See [Install WSL](https://docs.microsoft.com/en-us/windows/wsl/install) in the Microsoft documentation for more details. If you have chosen to use the Linux, you need to choose the Linux installation option for the mentioned tools.

## Update the EasyFranchise Application

1. Navigate into the [setup folder](../../../code/setup/)

   ``` bash
   cd code/setup
   ```

1. Execute the [Easy Franchise Deployment Script](../../../code/setup/easyfranchise-deployment.sh)

   ``` bash
   ./easyfranchise-deployment.sh
   ```

   You need to update the DB-Service, the EF-Service and the UI. As an alternative you can simply choose the Full-Deployment option which will conduct a complete deployment of all components.
   If you have problems in providing the neccessary information for the script have a look at the [deployment script description](TODO). 

## Deploy the Day 2 Application

The Day 2 application uses Helm charts as deployment descriptors. Using Helm charts for organizing kubernetes deployments is widely used in the kubernetes community. Helm offers a lot more possibilites in organzing applications. For our application we use a quite simple approach and defined each of the microserives as standalone chart with only a small set of values that will be replaced during the deploy process. If you want to learn more on helm charts you can have a look at the [Getting Started Guide](https://helm.sh/docs/chart_template_guide/getting_started/). 
For the Day 2 application you can have a look at the Helm charts in the [deployment folder](../../../code/day2-operations/deployment/helmCharts/). You should see three subfolders where each of them contains the chart definition of a component. In each of the chart folders you can find the following components: 

* Chart.yaml: Contains the information about the service which is beeing deployed by that chart
* values.yaml: Contains the variables that the helm template engine will replace within the deployment yaml files
* /templates: Contains the yaml definitions that are required by the service beeing described by the chart

The deployment consists of three components the [application router](../../../code/day2-operations/source/day2-approuter/), the [user interface](../../../code/day2-operations/source/day2-ui/) and the [day2 service](../../../code/day2-operations/source/day2-service/).

When we speak about **docker-repository**, we mean the combination of account and repo name that is usual with docker hub: `<docker account>/<repo name>`. 

1. Login to your dockerhub account:

   ``` bash
   docker login
   ```

1. Download the kubeconfig.yaml from the SAP BTP Cockpit and make it available to the kubectl
   1. Download the kubeconfig.yaml to your local disk
   1. Make the configuration available to kubcectl

      ```bash
   
      export KUBECONFIG=<path-to-kubeconfig>/kubeconfig.yaml
      ```

   1. Verify if you have the right config set

      ``` bash
      kubectl config current-context
      ```

      You should see something similar to that: shoot--kyma--c-6e7a0c3

1. Create Day 2 namespace

   ```bash
   kubectl create namespace day2-operations
   ```

1. Create registry secret (e.g. for docker hub)

   ```bash
   kubectl -n day2-operations create secret docker-registry registry-secret --docker-server=https://index.docker.io/v1/  --docker-username=<docker-id> 
   --docker-password=<password> --docker-email=<email>
   ```

1. Build and deploy the Day 2 service
   1. Navigate to the Day 2 service source folder. As the Day 2 service is based on spring we also use spring tools to build the image.

      ``` bash
      cd /code/day2-operations/source/day2-service
      ./mvnw spring-boot:build-image -DskipTests=true -Dspring-boot.build-image.imageName="<docker-repository>:day2-service-0.1"
      ```

   1. Push the image to the image registry

      ``` bash
      docker push <docker-repository>:day2-service-0.1
      ```

   1. Deploy the Helm chart to your cluster (make sure to navigate back to the root of the repository)

      ```bash
      helm upgrade "day2-service" "/code/day2-operations/deployment/helmCharts/day2-service-chart" --install --namespace day2-operations --set db.sqlendpoint="<HANA Cloud SQL Endpoint>" --set db.admin="<DB Admin User>" --set db.password="<DB Admin Password>" --set image.repository="<docker-repository>" --set image.tag="day2-service-0.1" --wait --timeout 300s --atomic
      ```

   1. If the deployment was successful you should see the follwowing output

      ``` bash
      Release "day2-service" has been upgraded. Happy Helming!
      NAME: day2-service
      LAST DEPLOYED: Mon May  9 15:50:59 2022
      NAMESPACE: default
      STATUS: deployed
      REVISION: 3
      TEST SUITE: None
      ```

1. Build and deploy the Day 2 approuter
   1. Build the docker image

      ```bash
      docker build --no-cache=true --rm -t "<docker-repository>:day2-approuter-0.1"  -f "code/day2-operations/deployment/docker/Dockerfile-day2-approuter" .
      ```

   1. Push the image

      ``` bash
      docker push <docker-repository>:day2-approuter-0.1
      ```

   1. Deploy the approuter

      ```bash
      helm upgrade "day2-approuter" "code/day2-operations/deployment/helmCharts/day2-approuter-chart" --install --namespace day2-operations --set clusterdomain="<kyma-cluster-domain>" --set image.repository="<docker-repository>" --set image.tag="day2-approuter-0.1" --wait --timeout 300s --atomic    
      ```

   1. If the deployment was successful you should see the follwowing output

      ``` bash
      Release "day2-approuter" has been upgraded. Happy Helming!
      NAME: day2-approuter
      LAST DEPLOYED: Mon May  9 15:49:58 2022
      NAMESPACE: default
      STATUS: deployed
      REVISION: 3
      TEST SUITE: None
      ```

1. Build and deploy the Day 2 ui
   1. Build the docker image

      ```bash
      docker build --no-cache=true --rm -t "<docker-repository>:day2-ui-0.1"  -f "code/day2-operations/deployment/docker/Dockerfile-day2-ui" .
      ```

   1. Push the image

      ``` bash
      docker push <docker-repository>:day2-ui-0.1
      ```

   1. Deploy the ui

      ```bash
      helm upgrade "day2-ui" "code/day2-operations/deployment/helmCharts/day2-ui-chart" --install --namespace day2-operations --set image.repository="<docker-repository>" --set image.tag="day2-approuter-0.1" --wait --timeout 300s --atomic
      ```

   1. If the deployment was successful you should see the following output

      ``` bash
      Release "day2-ui" has been upgraded. Happy Helming!
      NAME: day2-ui
      LAST DEPLOYED: Mon May  9 15:51:56 2022
      NAMESPACE: default
      STATUS: deployed
      REVISION: 3
      TEST SUITE: None
      ```
