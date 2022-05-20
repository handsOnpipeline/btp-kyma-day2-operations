#!/usr/bin/env bash

log() {
  # Print the input text in yellow.
  local yellow='\033[0;33m'
  local no_color='\033[0m'
  echo -e "${yellow}$*${no_color}"
}

read_automator_config() {  
  result=$(jq '.' /home/user/log/metadata_log.json)
  SUBDOMAIN=$(jq -r '."subdomain"' <<< "${result}")
  CLUSTER_DOMAIN=$(jq -r '."kymaDashboardUrl"' <<< "${result}")
  CLUSTER_DOMAIN="${CLUSTER_DOMAIN#*//console.}"
  KUBECONFIG_URL=$(jq -r '."kymaKubeConfigUrl"' <<< "${result}")
  DB_ADMIN="DBADMIN"
  DB_ADMIN_PASSWORD="$( echo "$result" | jq -r '.createdServiceInstances[] | select(.name == "hana-cloud") | .parameters.data.systempassword' 2> /dev/null)"
  
  DB_DASHBOARD="$( echo "$result" | jq -r '.createdServiceInstances[] | select(.name == "hana-cloud") | .statusResponse | .dashboard ' 2> /dev/null)"  
  DB_HOST="${DB_DASHBOARD#*?host=}"
  DB_HOST=${DB_HOST/&component*}
  DB_PORT=${DB_DASHBOARD/*port=/}
  DB_SQLENDPOINT="$DB_HOST:$DB_PORT"
}

echo "####################################################################################################"
echo "# Step 1 - Summary of Application Environment"
echo "####################################################################################################"
echo ""
read_automator_config

# Summary Step 1
echo
log "Deployment will be performed with the following attributes:"
echo ""
log "BTP Environment"
log "Provider Subdomain: " "$SUBDOMAIN"
log "Kyma Cluster Domain: " "$CLUSTER_DOMAIN"
log "Kubeconfig Url: " "$KUBECONFIG_URL"
echo ""
log "HANA Cloud"
log "SQL Endpoint: " "$DB_SQLENDPOINT"
log "DB Admin: " "$DB_ADMIN"
log "DB Admin Password: ********"
echo ""
echo ""

log "####################################################################################################"
log "# Step 2 - Deployment of Application Components"
log "####################################################################################################"
echo ""

log "Step 2.1 - Create Namepaces"
kubectl create namespace integration || true
kubectl create namespace backend || true
kubectl create namespace mock || true
kubectl create namespace frontend || true
kubectl create namespace day2-operations || true
echo

log "Step 2.2 - DB Secret: "
cat /home/user/tutorial/code/easyfranchise/deployment/k8s/db-secret.yaml | sed "s~<db-sqlendpoint>~$DB_SQLENDPOINT~g" | sed "s~<db-admin>~$DB_ADMIN~g" | sed "s~<db-admin-password>~$DB_ADMIN_PASSWORD~g" | kubectl apply -f - || true
echo

log "Step 2.3 - Backend Configmap"
kubectl apply -n backend -f /home/user/tutorial/code/easyfranchise/deployment/k8s/backend-configmap.yaml
kubectl apply -n integration -f /home/user/tutorial/code/easyfranchise/deployment/k8s/backend-configmap.yaml      
echo

log "Step 2.4 - BTP Service Deployment"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/btp-services.yaml" | sed "s~<provider-subdomain>~$SUBDOMAIN~g" | sed "s~<cluster-domain>~$CLUSTER_DOMAIN~g" | kubectl apply -f -
echo

PROJECT=approuter
log "Step 2.5 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_APPROUTER:$BTPSA_KYMA_IMAGE_TAG~g" | sed "s~<provider-subdomain>~$SUBDOMAIN~g" | sed "s~<cluster-domain>~$CLUSTER_DOMAIN~g" | kubectl apply -f -

PROJECT=db-service
log "Step 2.6 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_DB_SERVICE:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=bp-service
log "Step 2.7 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_BP_SERVICE:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=ef-service
log "Step 2.8 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_EF_SERVICE:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=broker
log "Step 2.9 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_BROKER:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=email-service
log "Step 2.10 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_EMAIL_SERVICE:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=ui
log "Step 2.11 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_UI:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=business-partner-mock
log "Step 2.12 - Deploy $PROJECT"
cat "/home/user/tutorial/code/easyfranchise/deployment/k8s/$PROJECT.yaml" | sed "s~<image-name>~$BTPSA_KYMA_IMAGE_NAME_BUSINESS_PARTNER_MOCK:$BTPSA_KYMA_IMAGE_TAG~g" | kubectl apply -f -

PROJECT=day2-approuter
log "Step 2.13 - Deploy $PROJECT"
helm upgrade "$PROJECT" "/home/user/tutorial/code/day2-operations/deployment/helmCharts/day2-approuter-chart" --install --namespace "day2-operations" --set clusterdomain="$CLUSTER_DOMAIN" --set image.repository="$BTPSA_KYMA_IMAGE_NAME_DAY2_SERVICE" --set image.tag="$BTPSA_KYMA_IMAGE_TAG" --wait --timeout 300s --atomic

PROJECT=day2-ui
log "Step 2.14 - Deploy $PROJECT"
helm upgrade "$PROJECT" "/home/user/tutorial/code/day2-operations/deployment/helmCharts/day2-ui-chart" --install --namespace "day2-operations" --set image.repository="$BTPSA_KYMA_IMAGE_NAME_DAY2_UI" --set image.tag="$BTPSA_KYMA_IMAGE_TAG" --wait --timeout 300s --atomic

PROJECT=day2-service
log "Step 2.15 - Deploy $PROJECT"
helm upgrade "$PROJECT" "/home/user/tutorial/code/day2-operations/deployment/helmCharts/day2-service-chart" --install --namespace "day2-operations" --set db.sqlendpoint="$DB_SQLENDPOINT" --set db.admin="$DB_ADMIN" --set db.password="$DB_ADMIN_PASSWORD" --set image.repository="$BTPSA_KYMA_IMAGE_NAME_DAY2_SERVICE" --set image.tag="$BTPSA_KYMA_IMAGE_TAG" --wait --timeout 300s --atomic  

echo
log "####################################################################################################"
log "# Deployment Successful"
log "####################################################################################################"
echo