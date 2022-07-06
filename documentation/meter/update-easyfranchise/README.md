# Update the Easy Franchise Application

As mentioned in the previous chapter, the Easy Franchise application needs to be updated to call the API provided by the Day2 service once a user starts the application. 


### Update the Easy Franchise Service

> **Hint:** be aware that you can use the [final code](https://github.com/SAP-samples/btp-kyma-day2-operations/tree/endresult) from the branch **endresult** if you encounter any issues.

1. If you haven't already done this, clone the GitHub repository and navigate to the folder [backend](../../../code/easyfranchise/source/backend/) where you will find the respective code for the Easy Franchise application.

1. Open the file [backend/ef-service/src/main/java/dev/kyma/samples/easyfranchise/EFService.java](../../../code/easyfranchise/source/backend/ef-service/src/main/java/dev/kyma/samples/easyfranchise/EFService.java) in your preferred editor.

1. Add some new methods within class **EFService** by pasting the following code to send the request to the API of the the Day2 app:

   ```java
   @PUT
   @Consumes(MediaType.APPLICATION_JSON)
   @Path("meter-user-login")
   public Response meterUserLogin(@Context HttpHeaders headers, @Context ContainerRequestContext resContext) {
       logger.info(Util.createLogDetails(resContext, headers));
       try {
           var tenantId = Util.validateTenantAccess(headers);
           
           //the user in plain 
           var user = getUser(headers);
           
           ConnectionParameter param = new ConnectionParameter(RequestMethod.PUT,
                   Util.getMeteringOperationServiceUrl() + "user/login").setAcceptJsonHeader();
           param.payload = "{\"tenantid\": \"" + tenantId + "\", \"user\": \"" + user + "\"}";
           Connection.call(param);
           if (param.status != HttpStatus.SC_OK) {
               throw new WebApplicationException("Error while calling metering day2 service.  "+ param.getUrl() + " status:" + param.status,  param.status);
           }
           return createOkResponse(param.content);
       } catch (WebApplicationException e) {
           logger.error(e.getMessage(), e);
           return createResponse(e);
       } catch (Exception e) {
           logger.error(UNEXPECTED_ERROR + e.getMessage(), e);
           return createErrorResponse();
       }
   }   
    /*
     * Get the user name from the request context. Return a default name for the local development
     * @param httpHeaders 
     */
    private static String getUser(HttpHeaders httpHeaders) throws Exception {
        if (Util.isLocalDev()) { // in the local run, we do not have a logged in user. We are just using a default string
            return "default-local-user-id";  
        }

         List<String> authorisationHeaders = httpHeaders.getRequestHeader("Authorization");
         if (authorisationHeaders.size()<1)
             throw new Exception("missing Header for key \"Authorization\".");
         
         // The user in plainext is taken. Consider encrypting if a higher privacy policy is needed.
         var user = getUserFromBearerToken(authorisationHeaders.get(0));
         return user; 

    }
   /**
    * Get the User from the bearerToken
    * @param bearerToken
    * @return
    * @throws Exception
    */
   private static String getUserFromBearerToken(String bearerToken) throws Exception {
       if (bearerToken.indexOf("Bearer") != 0)
           throw new Exception("The Bearer token of the header dose not not start with `Bearer `");
       try {
           String token = bearerToken.substring(7);
           String[] chunks = token.split("\\.");
           Base64.Decoder decoder = Base64.getUrlDecoder();

           // String header = new String(decoder.decode(chunks[0]));
           String payload = new String(decoder.decode(chunks[1]));

           JsonObject jsonObject = Json.createReader(new StringReader(payload)).readObject();
           return jsonObject.getString("user_name");
       } catch (Exception e) {
           throw new Exception("could not read user_name from Bearer token", e);
       }
   }   

   @OPTIONS
   @Path("meter-user-login")
   public Response setOptions10() {
       return createOkResponseSimpleText("ok");
   }
   ```

1. To make the application running locally, we use the file [hiddenconfig-template.properties](../../../code/easyfranchise/source/backend/shared-code/src/main/resources/hiddenconfig-template.properties) to store different properties. Copy this file and rename it to **hiddenconfig.properties**. 

1. Add the URL of the Day2 service as a property in the file **hiddenconfig.properties** so that the Easy Franchise service knows where to call the API. Here is the code that should be added:

   ```properties
   metering.operations.service: http://localhost:8091/
   ```

### Implementation Alternative with Approuter

In the above Java implementation, we select the bearer token and do the base64 decoding in Java. In the Approuter file [approuter-start.js](../../../code/easyfranchise/source/approuter/approuter-start.js) we already do a decoding, which can be reused. 
You can create a new header **x-user-id** with the user info as shown in the below code snipped:

```js
ar.beforeRequestHandler.use('/backend', function (req, res, next) {
    const token = req.user.token.accessToken;
    if (!token) {    
        res.statusCode = 403;
        res.end("Missing JWT Token");
    } else {
        const decodedToken = jwt_decode(token);
        const tenant = decodedToken && decodedToken.zid;
        req.headers['x-tenant-id'] = tenant;

        //get the user from decodedToken and create a according header: 
        const userId = decodedToken.user_name;
        req.headers['x-user-id'] = userId;

        next();
    }
});
```

With the above changes in the Approuter, the Java code for getting the user details would be then reduced to reading the header as follow:

```java
 public static String getUser(HttpHeaders httpHeaders) throws Exception {
   return httpHeaders.getHeaderString("x-user-id");
 }
```

## Update the UI

The UI is responsible to trigger and inform the Easy Franchsie service about a new login. 

1. Open the file [easyfranchise/source/ui/src/App.vue](../../../code/easyfranchise/source/ui/src/App.vue) in your preferred editor. 

1. Add a new function called **LogUser** to call the API of the Easy Franchise service. This can be added under ```methods: { }``` . Here is the code:  
   ```
    // Calling Metering API to register the user
    logUser(){
      const apiUrl = this.$backendApi + "/meter-user-login";
      fetch(
          apiUrl,
          {
            method: "PUT"  
          }
        )
        .then(response => {
          console.log("[DEBUG] Login user in EF Service: " + response);
        })
        .catch(err => {
          console.log(err);
        });
    },
   ```

1. Now we need to adapt the UI so that this method can been called every time the application is started. We are doing it by calling the method every time the UI is mounted. Search for the following section:
   ```
   mounted: function() {
     this.loadAllFranchises();
     this.loadAllCoordinators();
     this.checkandFillCompanyDetails();
   }
   ```  
   
1. Add the previously created method ``this.logUser();`` to the mount function as follow:
   ```
   mounted: function() {
     this.loadAllFranchises();
     this.loadAllCoordinators();
     this.checkandFillCompanyDetails();
     this.logUser();
   }
   ```
   
## Result

* You have implemented a new REST endpoint called **meter-user-login** in the Easy Franchise service. 
* You have updated the UI so that it calls the Easy Franchise API **meter-user-login** once the application is started.

