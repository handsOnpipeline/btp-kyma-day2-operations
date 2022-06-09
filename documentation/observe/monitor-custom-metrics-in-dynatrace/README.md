# Monitoring Kyma and Custom Workload Metrics with Dynatrace

## Introduction

Dynatrace is an Application Performance Management tool which provides full-stack insights into your application and its runtime environment. In this chapter, we will show how to enable monitoring metrics from both Kyma and your application in Dynatrace. 

## Getting Dynatrace Environment

As Dynatrace requires commercial license, different ways of requesting Dynatrace environments are described.

* Create a trial environment at [Dynatrace](https://www.dynatrace.com/trial/new/) with no cost.

* Purchase license through Dynatrace or SAP.

* As an SAP employee you can request an internal Dynatrace environment 

You only need to choose one of above options to request a Dynatrace environment. 

## Deploy Dynatrace Operator and Disable Istio Injection

Kyma uses Istio side-car injection to allow tracing of built-in components. The `dynatrace` namespace must be excluded from this injection for improved performance and stability. To disable Istio injection, see [Dynatrace for Kyma Monitoring: Disable Istio injection into Dynatrace](https://pages.github.tools.sap/apm/docs/installation/kyma/#initial-steps). 
Afterwards, you can deploy OneAgent on each Kubernetes cluster node via the Dynatrace Operator. For more information, see [Dynatrace for Kubernetes Monitoring](https://pages.github.tools.sap/apm/docs/installation/kubernetes/). 

In case you are using **Dynatrace trial environment**, this step is slightly different. Navigate to **Infrastructure** > **Kubernetes** > **Connect automatically via Dynatrace Operator**, fill in necessary information and follow the steps.

![](images/dynatrace_trial_deployoperator.png)

In addition, the settings page for connected Kyma cluster are also slightly different:

![](images/dynatrace_trial_settings.png)


## Configure Istio/Envoy Monitoring

Kyma comes with service-to-service communication and proxying (Istio-based service mesh). Istio and Envoy metrics can be ingested into Dynatrace via Prometheus integration and pre-build dashboard. To enable the metrics in Dynatrace, see [Configure Istio/Envoy Monitoring with Dynatrace](https://pages.github.tools.sap/apm/docs/installation/kyma/istio-envoy).

## Ingest Kyma Metrics/Custom Metrics

It is also possible to scrape metrics exposed by either Kyma endpoint or your application. These metrics can then be received, processed and exported by OpenTelemetry Collector. In our example, we are using Prometheus receiver and Dynatrace exporter to forward the metrics to Dynatrace. Based on these metrics, you can create your own dashboards for monitoring or alerting. There are two different options to scrape metrics described at [Ingestion of Kyma Metrics into Dynatrace](https://pages.github.tools.sap/apm/docs/installation/kyma/metrics).
We suggest that you use the second option **Scraping metrics with strict Istio peer authentication** which is the Kyma default behavior. 

> Note: In case you are using Dynatrace trial environment, the API_ENDPOINT would be different from the one specified in the documentation. 
> API_ENDPOINT defined by official documentation: https://apm.cf.sap.hana.ondemand.com/e/{ENVIRONMENT_ID}/api/v2/metrics/ingest
> API_ENDPOINT in trial environment: https://<trial environment ID>.live.dynatrace.com/api/v2/metrics/ingest

The **DB service** of the **Easyfranchise application** uses **Hibernate** as object-relational mapping framework to persist domain model entities to a relational database. The [SynchronizedConnectionMap java class](../../../code/easyfranchise/source/backend\db-service/src/main/java/dev/kyma/samples/easyfranchise/dbservice/SynchronizedConnectionMap.java) make use of the  **C3P0ConnectionProvider class**, provided by Hibernate. This connection provider  uses a [C3P0 connection pool](https://www.mchange.com/projects/c3p0/).

To illustrate how custom metrics are exposed in [Prometheus format](https://prometheus.io/docs/concepts/data_model/#metric-names-and-labels) through your own application, we provide the following example:
[C3P0 DB connection pool metrics](../../../code/day2-operations/source/day2-service/src/main/java/dev/kyma/samples/easyfranchise/day2/rest/jmx/C3P0ConnectionPoolMetricsScheduler.java) implemented using [Prometheus Java client](https://github.com/prometheus/client_java). In our DB-service [c3p0 pool](https://www.mchange.com/projects/c3p0/) is used to access the database of the Easy Franchise application. We collected the following metrics to provide a real-time monitoring of the database connecion usage for each of the subscribed tenants:
  - db_number_connections_all_users
  - db_number_idle_connections_all_users
  - db_number_busy_connections_all_users
  - db_min_pool_size
  - db_max_pool_size


The diagram below shows the flow of the custom metrics:

![](images/dynatrace_otel_custommetrics_flow.png)


1. C3P0 database connection pool is created by *db-service*. The pool metrics are collected periodically by *day2-service* via JMX. *day2-service* exposes collected metrics through an HTTP endpoint in Prometheus format as shown below:

```shell
# HELP db_number_idle_connections_all_users Number of idle database connections for all users
# TYPE db_number_idle_connections_all_users gauge
db_number_idle_connections_all_users{Tenant="DAY2ADMIN",} 10.0
db_number_idle_connections_all_users{Tenant="CITY-SCOOTER",} 10.0
# HELP db_number_busy_connections_all_users Number of busy database connections for all users
# TYPE db_number_busy_connections_all_users gauge
db_number_busy_connections_all_users{Tenant="DAY2ADMIN",} 0.0
db_number_busy_connections_all_users{Tenant="CITY-SCOOTER",} 0.0
# HELP db_min_pool_size Min number of database connections
# TYPE db_min_pool_size gauge
db_min_pool_size{Tenant="DAY2ADMIN",} 10.0
db_min_pool_size{Tenant="CITY-SCOOTER",} 10.0
# HELP db_max_pool_size Max number of database connections
# TYPE db_max_pool_size gauge
db_max_pool_size{Tenant="DAY2ADMIN",} 40.0
db_max_pool_size{Tenant="CITY-SCOOTER",} 40.0
# HELP db_number_connections_all_users Number of database connections for all users
# TYPE db_number_connections_all_users gauge
db_number_connections_all_users{Tenant="DAY2ADMIN",} 10.0
db_number_connections_all_users{Tenant="CITY-SCOOTER",} 10.0
```

2.  *OpenTelemetry collector* retrieves the metrics through the HTTP endpoints above with the following configurations. 
  > Note: both parameters **metrics_path** and **target** point to the HTTP endpoint URL (e.g.: http://day2-service.day2-operations.svc.cluster.local:8091/prometheus/metrics) 

```yaml
      prometheus:
        config:
          scrape_configs:
            - job_name: 'db_jmx_metrics'  ## c3p0 pool metrics
              tls_config:
                ca_file: /etc/istio-output-certs/root-cert.pem
                cert_file: /etc/istio-output-certs/cert-chain.pem
                insecure_skip_verify: true
                key_file: /etc/istio-output-certs/key.pem
              scheme: http 
              scrape_interval: 15s
              metrics_path: '/prometheus/metrics'  
              static_configs:
                - targets:
                  - 'day2-service.day2-operations.svc.cluster.local:8091'               
```
The complete file *otel-agent-mtls.yaml* with the configuration above can be found at the [otel-agent-mtls.yaml](../../../code/day2-operations/deployment/k8s/otel-agent-mtls.yaml) file.

3. With the *Dynatrace exporter* from the *OpenTelemetry collector*, the metrics are forwarded to the Dynatrace instance. In Dynatrace, you can create a dashboard based on the custom metrics above. 

![](images/dynatrace_custommetrics.png)


### JMX Metrics for C3P0 Connection Pool

The flow for retrieving C3P0 connection pool metrics is discussed in the previous section. In this section, we will briefly discuss some implementation considerations.

- In *day2-service*, we implemented a scheduler to poll the metrics perodically through JMX port 9999 of db-service. 

```java
  public C3P0ConnectionPoolMetricsScheduler(@Value("${jmx.remote.host}") String remoteHost, @Value("${jmx.remote.port}") int remotePort) {

    this.attributeClient = new JmxMBeanClient(remoteHost, remotePort);  // create jmx client with db-service host and port

    C3P0GaugeController.createSingletonInstance();
  }

  @Scheduled(fixedRateString = "5000", initialDelayString = "0")
  public void schedulingTask() {

    logger.info("schedulingTask get called, updating metrics");

    C3P0GaugeController.retrieveMetrics(attributeClient);  // retrieving metrics from jmx port of db-service
  }

```

- To enable the access to *db-service* JMX port, simply adapt the following command line parameters in the [Dockerfile-db-service](../../../code/easyfranchise/deployment/docker/Dockerfile-db-service).

```Diff

# Expose jmx port 9999
+EXPOSE 9999

# start the db-service normally
-CMD ["java", "-cp",  "/opt/app/*", "dev.kyma.samples.easyfranchise.ServerApp", "8080"]

# start the db-service with jmx port enabled
+CMD ["java", "-Dcom.sun.management.jmxremote.port=9999", "-Dcom.sun.management.jmxremote.authenticate=false", "-Dcom.sun.management.jmxremote.ssl=false", "-cp",  "/opt/app/*", "dev.kyma.samples.easyfranchise.ServerApp", "8080"] 
```

Enable the JMX port access within the cluster by adding the following configuration to [db-service.yaml](../../../code/easyfranchise/deployment/k8s/db-service.yaml)

```yaml
---
apiVersion: v1
kind: Service
metadata:
  name: db-jmx-metrics
  namespace: integration
  labels:
    app: db-jmx-metrics
spec:
  type: ClusterIP
  ports:
    - port: 9999
      protocol: TCP
      targetPort: 9999  
  selector:
    app: db-service
```

- We are using [Prometheus Java client](https://github.com/prometheus/client_java) to store and expose the metrics in Prometheus format. There is another popular package [Micrometer](https://micrometer.io/) which could serve a similar purpose. However, we decided not to use it as it [lacks native support of dynamic label for gague](https://github.com/micrometer-metrics/micrometer/issues/535) (In Micrometer the term *Tag* is used, whereas in Prometheus *Label* is used).  In our application, we need to be able to add a label to metrics dynamically based on the subscribed tenant name, such as *CITY-SCOOTER*. Prometheus Java client supports **defining label name** and **adding lable value** at a separate time.

```java
    static String LABEL_NAMES = "Tenant";
    
    // first, define label name without provide lable value
    dbNumIdleConnectionsAllUsers =  Gauge.build()
                                    .name("db_number_idle_connections_all_users")
                                    .help("Number of idle database connections for all users").labelNames(LABEL_NAMES)
                                    .register(); 
                                    

    // later adding lable value only when corresponding metric is avaiable

    Object label = attributeClient.retrieveAttributeValue(bean, LABEL_SOURCE, false, null); // label = subaccount ID
    logger.info("Retrieving C3P0 Pool Metrics for label:" +  label.toString() );

    AttributeList attrList = attributeClient.retriveAttributeList(bean, attrListString);
    for(Attribute a : attrList.asList()){
        String attrName = a.getName();
        if(attrName.equalsIgnoreCase(ATTR_NUM_IDLE_CONNECTIONS_ALL_USERS)){
            dbNumIdleConnectionsAllUsers.labels(label.toString()).set((Integer) a.getValue());    // adding lable value when setting corresponding metric value
        }
    ... 
    }
```

- When deploying our **day2-service** in Kyma, we need to disable the Istio injection which prevents the OpenTelemtry collector from accessing the exposed HTTP endpoint properly.

```yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  labels:
    app: day2-service
  name: day2-service
  namespace: day2-operations
spec:
  replicas: 1
  selector:
    matchLabels:
      app: day2-service
  template:
    metadata:
      labels:
        app: day2-service
        sidecar.istio.io/inject: "false" # disable istio injection, otherwise facing "connection reset by peer" error
```
