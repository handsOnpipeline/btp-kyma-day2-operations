package dev.kyma.samples.easyfranchise.day2.rest.jmx;

import java.lang.Integer;
import java.util.List;
import io.prometheus.client.Gauge;
import javax.management.Attribute;
import javax.management.AttributeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class C3P0GaugeController {

    protected static String C3P0_JMX_DOMAIN = "com.mchange.v2.c3p0:*,type=PooledDataSource";

    protected static String LABEL_NAMES = "Tenant";
    protected static String LABEL_SOURCE = "user";
    protected static String ATTR_NUM_IDLE_CONNECTIONS_ALL_USERS = "numIdleConnectionsAllUsers";
    protected static String ATTR_NUM_BUSY_CONNECTIONS_ALL_USERS = "numBusyConnectionsAllUsers";
    protected static String ATTR_NUM_Connectioons_ALL_USERS = "numConnectionsAllUsers";
    protected static String ATTR_MAX_POOL_SIZE = "maxPoolSize";
    protected static String ATTR_MIN_POOL_SIZE = "minPoolSize";

    protected static String[] attrListString = {ATTR_NUM_IDLE_CONNECTIONS_ALL_USERS, ATTR_NUM_BUSY_CONNECTIONS_ALL_USERS, ATTR_NUM_Connectioons_ALL_USERS, ATTR_MAX_POOL_SIZE, ATTR_MIN_POOL_SIZE};
    
    static Gauge dbNumIdleConnectionsAllUsers;
    static Gauge dbNumBusyConnectionsAllUsers;
    static Gauge dbNumConnectionsAllUsers;
    static Gauge dbMaxPoolSize;
    static Gauge dbMinPoolSize;

    private static final Logger logger = LoggerFactory.getLogger(C3P0GaugeController.class);

    public C3P0GaugeController() {
             
    }

    public static void createSingletonInstance(){

        if(dbNumIdleConnectionsAllUsers == null){
            dbNumIdleConnectionsAllUsers =  Gauge.build()
                                            .name("db_number_idle_connections_all_users")
                                            .help("Number of idle database connections for all users").labelNames(LABEL_NAMES)
                                            .register();      // registry using default defaultRegistry  
        }

        if(dbNumBusyConnectionsAllUsers == null){
            dbNumBusyConnectionsAllUsers =  Gauge.build()
                                            .name("db_number_busy_connections_all_users")
                                            .help("Number of busy database connections for all users").labelNames(LABEL_NAMES)
                                            .register();
        }

        if(dbNumConnectionsAllUsers == null) {
            dbNumConnectionsAllUsers =  Gauge.build()
                                        .name("db_number_connections_all_users")
                                        .help("Number of database connections for all users").labelNames(LABEL_NAMES)
                                        .register();     
        }

        if(dbMaxPoolSize == null) {
            dbMaxPoolSize = Gauge.build()
                            .name("db_max_pool_size")
                            .help("Max number of database connections").labelNames(LABEL_NAMES)
                            .register();       
        }
        
        if(dbMinPoolSize == null) {
            dbMinPoolSize = Gauge.build()
                            .name("db_min_pool_size")
                            .help("Min number of database connections").labelNames(LABEL_NAMES)
                            .register();           
        }
    }
    public static void retrieveMetrics(JmxMBeanClientInterface attributeClient){

        if(attributeClient == null)
            return;

        try {
            attributeClient.openConnection();

            List<String> beanList = attributeClient.retrieveBeanNames(C3P0_JMX_DOMAIN);

            for(String bean : beanList){
                logger.info(bean);

                Object label = attributeClient.retrieveAttributeValue(bean, LABEL_SOURCE, false, null); // label = subaccount ID
                logger.info("Retrieving C3P0 Pool Metrics for label:" +  label.toString() );

                AttributeList attrList = attributeClient.retriveAttributeList(bean, attrListString);
                for(Attribute a : attrList.asList()){
                    String attrName = a.getName();
                    if(attrName.equalsIgnoreCase(ATTR_NUM_IDLE_CONNECTIONS_ALL_USERS)){
                        dbNumIdleConnectionsAllUsers.labels(label.toString()).set((Integer) a.getValue());
                    } else if(attrName.equalsIgnoreCase(ATTR_NUM_BUSY_CONNECTIONS_ALL_USERS)){
                        dbNumBusyConnectionsAllUsers.labels(label.toString()).set((Integer) a.getValue());
                    } else if(attrName.equalsIgnoreCase(ATTR_NUM_Connectioons_ALL_USERS)){
                        dbNumConnectionsAllUsers.labels(label.toString()).set((Integer) a.getValue());
                    } else if(attrName.equalsIgnoreCase(ATTR_MAX_POOL_SIZE)){
                        dbMaxPoolSize.labels(label.toString()).set((Integer) a.getValue());
                    } else if(attrName.equalsIgnoreCase(ATTR_MIN_POOL_SIZE)){
                        dbMinPoolSize.labels(label.toString()).set((Integer) a.getValue());
                    }
                }
          }
    

        } catch (Exception e) {
            logger.error("Retrieve JMX value error: ", e);
        } finally {
            try {
                attributeClient.closeConnection();    
            } catch (Exception e) {
                logger.error("Close JMX connection error: ", e);
            }
        }
    }
}
