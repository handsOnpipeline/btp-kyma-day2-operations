# Run the Metering Scenario End-2-End Locally
 
This chapter explains the steps to run all services involved in the metering scenario locally. This can be done on Windows, Mac or Linux.

TODO Math: add Solution Digramm with all relevant services 

Here the list of Services to be stated locally: 
* Database Service
* Easy Franchise Service
* Business Partner Service
* Day2 Service
* Easyfranchise UI
* Metering Dashboard UI

## Start the Database Service, the Easy Franchise Service, and the Business Partner Service

### Prerequisites
- You have prepared the SAP HANA Cloud properties for a JDBC connection. 
- You have an SAP S/4HANA Cloud system or a Business Partner mock server up and running 
<!-- TODO: give details where to find info if this is not done -->

### Configure hiddenconfig.properties file

To run those services locally, you have to configure some properties in a `hiddenconfig.properties` file:
1. Open the prepared sources from the previous steps or download the final once from the GitHub [Repository](https://github.com/SAP-samples/btp-kyma-multitenant-extension-day2/tree/day2-final/code/easyfranchise/source/backend). In Branch **day2-final** you find the source in folder [code/easyfranchise/source/backend](https://github.com/SAP-samples/btp-kyma-multitenant-extension-day2/tree/day2-final/code/easyfranchise/source/backend).
   
<!-- TODO check if this link works! -->

1. Copy the file ```code/backend/shared-code/src/main/resources/hiddenconfig-template.properties``` to `hiddenconfig.properties` in the same folder.

1. Maintain your SAP HANA Cloud JDBC connection properties in the `db.*` section. This should look like this:
   ```
   db.name: EasyFranchiseHANADB
   db.sqlendpoint: your_hostname.hanacloud.ondemand.com:443
   db.admin: EFADMIN
   db.password: your_efadmin_password
   ```

   See [How to find JDBC Connection Properties](https://github.com/SAP-samples/btp-kyma-multitenant-extension/tree/main/documentation/prepare/configure-hana#how-to-find-jdbc-connection-properties) for more details.

1. Update the `s4hana.destination.*` properties.

   If you use the SAP Business Partner mock server to run the application locally, use:

   ```
   s4hana.destination.URL: http://localhost:8081
   s4hana.destination.User:
   s4hana.destination.Password:
   s4hana.destination.Authentication: NoAuthentication
   s4hana.destination.Type: http
   ```

   If you are using your SAP S/4HANA Cloud system, copy the following snippet updated with your values:

   ```
   s4hana.destination.URL: https://xxxxxxxx-api.s4hana.ondemand.com
   s4hana.destination.User: <your Communicatrion Arragement User>
   s4hana.destination.Password: <Password of the Communication User>
   s4hana.destination.Authentication: BasicAuthentication
   s4hana.destination.Type: http
   ```
### Build the project
1. Open a command line window and change to directory to ```code/backend``` containing the main '''pom.xml'''. Run the following Maven command:

   ```mvn clean install```

   > Info: When running this command the first time, many JAR files will be downloaded to your local Maven repository.

   The second run will be faster as all these downloads will be no longer necessary.

   You should see a successful build of all four modules and an allover **BUILD SUCCESS**:

   ```
      [INFO] Reactor Summary for easyfranchise 1.0-SNAPSHOT:
      [INFO]
      [INFO] easyfranchise ...................................... SUCCESS [  0.784 s]
      [INFO] shared-code ........................................ SUCCESS [ 18.696 s]
      [INFO] db-service ......................................... SUCCESS [  9.189 s]
      [INFO] bp-service ......................................... SUCCESS [  4.606 s]
      [INFO] ef-service ......................................... SUCCESS [  6.742 s]
      [INFO] ------------------------------------------------------------------------
      [INFO] BUILD SUCCESS
      [INFO] ------------------------------------------------------------------------
   ```

   In each project folder, a new folder **target** is created containing the build result.

### Start All Backend Services

1. Run the following commands to start the services. Start each in a separate command line window and in the correct folder.

   In folder [code/backend/ef-service](/code/backend/ef-service):

   ||command (``> cd ef-service``)|
   |:-----|:----|
   |windows|```java -cp ".\target\*;.\target\dependency\*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.EFServer 8080```|
   |unix   |```java -cp "./target/*:./target/dependency/*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.EFServer 8080```|


   In folder [code/backend/bp-service](/code/backend/bp-service):

   ||command (``> cd bp-service``)|
   |:-----|:----|
   |windows|```java -cp ".\target\*;.\target\dependency\*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.ServerApp 8100```|
   |unix   |```java -cp "./target/*:./target/dependency/*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.ServerApp 8100```|

   In folder [code/backend/db-service](/code/backend/db-service):

   ||command (``> cd db-service``)|
   |:-----|:----|
   | windows | ```java -cp ".\target\*;.\target\dependency\*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.ServerApp 8090```|
   | unix    | ```java -cp "./target/*:./target/dependency/*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.ServerApp 8090```|


   Each service will run on a different port (8080, 8100, 8090). Don't use different ones. The `hiddenconfig.properties` relies on them!

   >*Hint:* Just in case you want to debug one of the applications using port `8888` you can start the Java process using the following command. Then, connect with your IDE to the external Java process on port `8888`.

   >```
   >java -Xdebug -Xrunjdwp:transport=dt_socket,address=8888,server=y,suspend=y -cp "./target/*;./target/dependency/*" -Dlocal_dev=true dev.kyma.samples.easyfranchise.ServerApp <port>
   >```

### Test APIs

1. Check that you can get all mentors:
   ``` 
   curl  --request GET 'http://localhost:8080/easyfranchise/rest/efservice/v1/mentor' 
   ```

   > Note: If the request fails, check the logs of the ef-service and the db-service.

1. Check that you can read franchise
   ```
   curl --request GET 'http://localhost:8080/easyfranchise/rest/efservice/v1/franchisee' 
   ```
   > Note: If the request fails, check the logs of the ef-service and the db-service.


## Run the Day2 Service

1. Open ```http://localhost:8091``` in browser to check if the Operations Service is already started. You should get:
   
   ![](../images/operationsServiceStartPage.png)
1. If server is not started start him.

## Run the Easyfranchise UI

1. Check that you have defined the URL path of the backend apis to the local backend services. Open the file ```code/easyfranchise/source/ui/src/main.js](/code/easyfranchise/``` and check the value for ```Vue.prototype.$backendApi``` for:
   
   ```js
   Vue.prototype.$backendApi = "http://localhost:8080/easyfranchise/rest/efservice/v1";
   ```
1. Open a new terminal and change directory to **ui** 

   ```shell
   $ cd ui
   ```

1. Install node modules in your repository by running:

   ```shell
   $ npm install
   ```

1. Run the server:

   ```shell
   $ npm run serve
   ```
   As result the app should show where its running. 
   By default this is at: 

   ```
   http://localhost:8081/
   ```
1. Open this URL in the browser.
   
## Run the Metering Dashboard UI

1. Open a command window and go [code/metering-dashboard/source/ui](../../code/metering-dashboard/source/ui)

1. Install NodeJs Modules
    
   ```shell
   $ npm install
   ```
   
1. Start the Service
 
   ```shell
   $ npm run serve
   ```
   
   As result the app should show where its running. 
   By default this is at: 

   ```
   http://localhost:8082
   ```
1. Open this URL in the browser.

1. As you already did logged in to the EasyFranchise service, which is using the tenantid 123456789-local-tenant-id, you should find an according record. 
   
   ![](../images/meeteringDashboardLocaltenant.png)

   TODO replace picture& texte above if we have subacount display name.

1. If you would like to see a second tenant or increase the number of active users you can reach this by : 
   - update the properties ```devmode.tenantid``` in the ```hiddenconfig.properties``` of backend services.  Stop, build and restart again, so that the new tenantid gets activated and reopen the Easyfranchise ui
   - much easier is to run a Rest Calls against the **Operations Service** via curl command and fake a user login of eg "Jon Smith" for "second-local-tenant-id": 
   
     ```shell
     curl --request PUT 'http://localhost:3000/user/login' \
     --header 'Content-Type: application/json' \
     --data-raw '{"tenantid": "second-local-tenant-id", "user": "Jon Smith"}
     ```
     
<!-- TODO: To be moved to Trouble shooting section -->
>**Trouble shooting hint: no active user metering values shown?** 
> - Check in the console of the **Operations services** the return status of the ```user/metric``` call. In case it is 200 as shown in the screenshot below, you have forgotten to set the envitoment variable to indicate the local run. Set ```local_dev=true``` and restart the opeations service again. 
>
>   ![](../images/failingCrosUserMetricCall.png)
> * Run the Rest call agains the Operations service (replace year and month!) and check the result and logs. 
>   ```
>   curl --request GET 'http://localhost:3000/user/metric?year=2022&month=3'
>   ```

## Result

* You understand now how to run a local test of all microservices and know the limitations of the local Run. 
