# Build a Script to Automate the Deployment

In the setup folder, there is a shell script [full-deployment.sh](../../../code/setup/full-deployment.sh). Calling the script will guide you through the whole deployment of the application. There is also the option to do a dry run in order to validate the yaml files that will be used for deployment. 

## Preparation

If you run through the manual deployment before, revert the changes you have done in the `.yaml` files. The build scripts will modify them according to your input. 


## Deployment Script

Execute build script from within the [setup folder](../../../code/setup/): 

```shell
./full-deployment.sh
```

Execute build script in dry-run mode: 

```shell
./full-deployment.sh --dry-run
```

The script allows to save the entered values as a json file within the setup folder. Please note that the passwords will be stored in plain text. If you run the script multiple times it will offer the user to read the configuration from the setup folder if it's present. 

If you are using the script to deploy the application the first time it is recommended to choose the `Full-Deployment` options from the list given by the script. 

```shell 
Choose component for deployment or deploy the whole application
1) Approuter
2) DB-Service
3) BP-Service
4) EF-Service
5) SaaS-Broker
6) Email-Service
7) UI
8) Mock-Server
9) Full-Deployment
#?
```

This will make sure that the components will be deployed in the right order. If you already have the application deployed you can deploy single components as well. 