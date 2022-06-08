# Discover the Metering Use Case

Measuring the usage of a business service or applications is useful for commercial purposes such as billing, license compliance, and cost assessments.

Which measurable values can be used for commercial purposes depends on the business logic of the your application. In case of multitenancy, those values should be of course transparent to the respective tenant. 

As the number of active users is certainly a good example of metering, lets have a deeper look at it. Here is the user story:

* As a SAP Partner, I would like to know the number of active users per month and per tenant, so that I can check those numbers against the sold licenses and use thoses metrics as basis for billing.
* As a SAP Partner, I want to have a dashboard, where I can see the number of active users and eventually other metering metrics, so that I can quickly have a good overview of the application usage.

In the next chapters you will learn how to update the multitenant application Easy Franchise to meter users logins per month.





