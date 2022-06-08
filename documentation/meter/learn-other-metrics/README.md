# Learn more about other Business Logic Metrics

When looking at the **Easyfranchise Application**, you might want to meter other metrics. 
Eg. you would like to count when a email is send or whenever a new Mentor is assigned. 

In your own scenario you might face completely different requirements. 

For all of those you can use an similar approach as we showcased for the **Active Users**.
Just plugin to your java services and provide your meetering info to the **Operatons Service** which can persist the info in according database table of the  **Metering Database Schema**. Provide a new fitting new REST endpoint to the **Operations Service** to replay your metering data, which can be called by the UI .

## Result

* You are able to transfer the concept of metering to your own business problem.