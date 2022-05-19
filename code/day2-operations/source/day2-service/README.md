# Operations Service

## How To Run

1. The simplest way to provide your custom properties is to configure them via a file outside of the jar.

   Copy  [application-template.properties](application-template.properties) to ``application.propertie`` in same folder. Edit the ``application.propertie`` and fill in your datasource values. 
2. To run the application you have the choice between various options:
   
   - using maven and the spring-boot plugin:   
  
     ```
     $ ./mvnw spring-boot:run -Dspring.config.location="application.properties"
     ```

     with remote debug option: 
     ```
     $ ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=8888" -Dspring.config.location="application.properties"
     ```
   - Build and execute via jar: 
  
     ```
     $ ./mvnw clean package
     $ java -jar target/operations-service-0.0.1-SNAPSHOT.jar -Dspring.config.location="application.properties"
     ``` 

  > Hint: in case you would like to use a application.properties file on another file location youc an also spezfiy do so by using: 
  > ```
  > -Dspring.config.location="file:///Users/home/config/application.properties"
  > ```
  
## The Rest API

1. Announce that a user has logged in:

   ```
   curl --request PUT 'http://localhost:8080/user/login' \
   --header 'Content-Type: application/json' \
   --data-raw '{"tenantid":"tenantid123456789","user":"John.Smith@acomp.com"}'
   ```
2. Get User metric for April 2022. Please update month and year to the current one to get the agregated metric fitting to the above PUT request.

   ```
   curl --location --request GET 'http://localhost:8080/user/metric?year=2022&month=4'
   ```

   here an sample json response: 
   ```
   [
       {
           "tenantid": "tenantid123456789",
           "activeUsers": 3
       },
       {
           "tenantid": "tenantid000000000",
           "activeUsers": 1
       }
   ]
   ```
