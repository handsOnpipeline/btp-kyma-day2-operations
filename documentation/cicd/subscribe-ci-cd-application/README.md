# Subscribe to the SAP Continuous Integration and Delivery Service

The following chapter can be skipped if you have used the btp-setup-automator for preparing your subaccount as the necessary steps have already been performed. 

## Add Entitlement for the Continuous Integration & Delivery Service

> **NOTE** If you used the btp-setup-automator script for this mission, the entitlements, subscription and role assignment are already done for you. Skip these steps and continue with *Create Continuous Integration & Delivery Job for Easy Franchise UI*.

1. Navigate to **Entitlements** within the **EasyFranchise** subaccount.
2. Choose **Configure Entitlements**.
   
   ![](./images/01-Entitlement-01.png)
3. Choose **Add Service**.
4. In the **Entitlements** dialog:

   * Search for **Continuous Integration & Delivery**.
   * Select the **free (Application)** plan.
   * Choose **Add 1 Service Plan**.

     ![](./images/01-Entitlement-02.png)

## Create the Application Subscription

1. In the **EasyFranchise** subaccount, navigate to **Service Marketplace**.
2. Choose the **Continuous Integration and Delivery** tile.
3. Choose **Create**.
4. In the **New Instance Subscription** dialog, the plan **free** should be preselected. Choose **Create**.

   ![](./images/02-Subscription.png)

## Assign the Continuous Integration & Delivery Roles to Your User

1. In the **EasyFranchise** subaccount, choose **Security** > **Users**.
2. Search for your user and select it.
3. Choose **Assign Role Collection**.

   ![](./images/03-Role-Assignement.png)
4. In the **Assign Role Collection** dialog, assign the **CICD Service Administrator** and the **CICD Service Developer** role and choose **Assign Role Collection**.

   ![](./images/03-Role-Assignement-02.png)
