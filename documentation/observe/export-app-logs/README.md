# Export Application Logs to External Logging Tools

## Introduction

By default, a Fluent Bit instance, a lightweight log processor and forwarder, has been configured in the **kyma-system** namespace to collect logs from multiple sources. Then, it forwards the logs to a Loki (scalable log aggregation system from Grafana) instance inside the **kyma-system** namespace. To access the Loki instance, see [Exposing built-in Grafana securely with Identity Authentication(IAS)](/documentation/observe/expose-grafana-with-ias/README.md). However, the Fluent Bit instance in the **kyma-system** namespace cannot be extended to forward logs to external tooling. This tutorial describes how to install a custom log collector in a custom namespace, then export the logs to an external logging tools such as an ELK stack (Elasticsearch and Kibana).

The diagram below shows the target setup of our scenario.  

1. A second Fluent Bit instance is installed in a custom namespace. It extracts all logs avaiable in the Kyma cluster, including logs from the Easy Franchise Application.

1. Additionally, the logs from the Kyma components are collected as well. It is also possible to exclude the **kyma-system** namespace from the log collection.

1. The Fluent Bit instance is configured to forward all logs to an ELK stack. 

1. An ELK stack is installed in a separate Kyma cluster, serving as an external log viewing tool. If a separate cluster is not available, you could also use the same cluster as  the Easy Franchise application. The concept would be rather similar. 

1. A Day2 operator would accesss the logging tool via browser. 

![](images/custom_fluentbit.png)


## Install ELK Stack in a Kyma Cluster

In this step we use an ELK stack as an example for external logging tooling. You can certainly choose any other tools as long as the protocol is supported by the log collector. 

The default configurations will install the ELK stack in the same Kyma cluster where the Easy Franchise application is deployed. This is to accommodate the situation where only one Kyma cluster is available (for example, in case of using SAP BTP free tier).  If you do have a second Kyma cluster, you can opt to install the ELK stack in a seperate cluster. The setup is very similar.

We are using the official [Elastic Cloud on Kubernetes](https://www.elastic.co/guide/en/cloud-on-k8s/current/index.html) provided by Elastic. It is essentially a set of Kubernetes operators orchestrating lifecycle of Elasticsearch, Kibana and a few other Elastic resources. In our example, we only need to deploy Elasticsearch and Kibana customer resource definition.

> Note: In the diagram above, the ELK stack is depicted as "External Custom Logging Stack" in a second Kyma cluster to illustrate better the flexibility of the scenario. You can certainly deploy ELK in the same Kyma cluster where your Easy Franchise application is running.

1. Install Elastic CRD and the operator with its RBAC rules.
```shell
# https://www.elastic.co/guide/en/cloud-on-k8s/1.9/k8s-deploy-eck.html

kubectl apply -f https://download.elastic.co/downloads/eck/1.9.1/crds.yaml

kubectl apply -f https://download.elastic.co/downloads/eck/1.9.1/operator.yaml

```

2. Deploy a new instance of Elasticsearch and Kibana respectively. 


```shell
# Make sure move to folder code/day2-operations/deployment/k8s/elk-stack

# Deploy Elasticsearch CRD and expose elasticsearch service via APIRule


kubectl apply -f elastic_on_cloud.yaml

kubectl apply -f elastic-expose.yaml


# Deploy Kibana CRD and expose kibana service via APIRule

kubectl apply -f kibana.yaml

kubectl apply -f kibana-expose.yaml


```

3. Once the resources above are deployed, you can check the status of the deployment with the following command:

```shell
$ kubectl get elastic

NAME                                                          HEALTH   NODES   VERSION   PHASE   AGE
elasticsearch.elasticsearch.k8s.elastic.co/ef-elasticsearch   green    3       7.15.0    Ready   41m

NAME                                     HEALTH   NODES   VERSION   AGE
kibana.kibana.k8s.elastic.co/ef-kibana   green    1       7.15.0    38m

```

In addition, you can also access Kibana and Elasticsearch in your browser. To find out the URL, run following commands:

```shell

$ kubectl get virtualservices.networking.istio.io

NAME                   GATEWAYS                                         HOSTS                                                 AGE
elastic-expose-qwrn4   ["kyma-gateway.kyma-system.svc.cluster.local"]   ["elasticsearch.<your Kyma cluster domain>.kyma.ondemand.com"]   47m
kibana-expose-s855g    ["kyma-gateway.kyma-system.svc.cluster.local"]   ["kibana.<your Kyma cluster domain>.kyma.ondemand.com"]          47m

```

When logging in, you can use  "elastic" as username, and get the password in your cluster with following command:

```shell

kubectl get secret ef-elasticsearch-es-elastic-user -o go-template='{{.data.elastic | base64decode}}'

```

The **login credentials**  will also be needed for configuring the Fluent Bit instance in the next step.

## Install A Second Fluent Bit in Custom Namespace

We will install the log collector Fluent Bit in a custom namespace.  Fluent Bit collects logs on all nodes in the Kyma cluster and forwards to Elasticsearch which is deployed in the previous step. Open the file [fluentbit.yaml](/code/day2-operations/deployment/k8s/fluentbit.yaml), and replace the following parameter with your own value:

- Adapt the value for "FLUENT_ELASTICSEARCH_HOST" with your Elasticsearch URL which was retrieved in the previous step, for example "elasticsearch.<your Kyma cluster domain>.kyma.ondemand.com"

![](images/fluent_elasticsearch_host.png)

- Fluent Bit requires username and password in order to forward logs to Elasticsearch.  We are using envrionment variables `ELASTIC_USER` (default to "elastic") and `ELASTIC_PASS` to pass the value.  If Elasticsearch is deployed in the same cluster as your workload, the password for Elasticsearch has been created automatically and saved in the `ef-elasticsearch-es-elastic-user` secret, and you can skip this step.  However, if Elasticsearch is deployed in a different cluster (referred as **ELK cluster**), extract the Elasticsearch password from the ELK cluster, then create a secret in the Easy Franchise application cluster as shown below:

```shell
# Make sure you are in the ELK cluster, and extract the password of Elasticsearch.

kubectl get secret ef-elasticsearch-es-elastic-user -o go-template='{{.data.elastic | base64decode}}'

# Now switch to Easy Franchise cluster, and create a secret using the extracted value above.
kubectl create secret generic ef-elasticsearch-es-elastic-user --from-literal=elastic=<output from previous command>

```

Then, deploy the **fluentbit.yaml** in your Kyma cluster. 

> Note: Please deploy fluentbit in the Kyma cluster where your workload (e.g. EasyFranchise app) is running.

```shell

kubectl deploy -f fluentbit.yaml

```

## Check the Logs in Kibana

To check if the logs have been properly pushed into Elasticsearch, you can try the following URL in your browser: ```https://elasticsearch.<your Kyma cluster>.kyma.ondemand.com/_cat/indices```

![](images/elasticsearch_indices.png)

You should see an index with prefix "fluentbit". 

1. In your Kibana, go to "Index Management" (```https://kibana.<your Kyma cluster>.kyma.ondemand.com/app/management/data/index_management/indices```), you should also see the fluentbit indices.

![](images/elasticsearch_indices2.png)

2. Go to "Index Pattern" (```https://kibana.<your Kyma cluster>.kyma.ondemand.com/app/management/kibana/indexPatterns```) to create a new index pattern.

![](images/index_pattern.png)

3. Go to "Analytics --> Discover" to see the logs.

![](images/discover_logs.png)


## Troubleshooting

- Check the PVC (PersistentVolumeClaim) usage of Elasticsearch in Kyma cluster

  * Option 1: command line

    ```shell

    kubectl -n <namespace> exec <pod-name> df

    # for example
    kubectl exec -it ef-elasticsearch-es-default-2 df

    ```
  
  * Option 2: Prometheus metrics dashboard
    
    Open the build-in Grafana dashboard, navigate to Dashboard > Manage, then search and select the **Persistent Volumes** dashboard.

    ![](images/troubleshooting_kubelet_pvc_metrics_navigate.png)


    ![](images/troubleshooting_kubelet_pvc_metrics.png)


- To clean up the Elasticsearch index, you can deploy a curator as shown below:

  ```shell
    kubectl apply -f curator.yaml
  ```

  You can check the cronjob schedule with following command:

  ```shell
    kubectl get cronjob
  ```
