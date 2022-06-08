package dev.kyma.samples.easyfranchise.day2.rest.jmx;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class C3P0ConnectionPoolMetricsScheduler { // TODO: what was the reason to name this class C3P0? please add  class documentation

  private static final Logger logger = LoggerFactory.getLogger(C3P0ConnectionPoolMetricsScheduler.class);

  private JmxMBeanClientInterface attributeClient; 

  // cannot use in following way as value injection happens only after constructor call finishes
  // @Value("${jmx.remote.host}")  
  // private String remoteHost;
  
  // @Value("${jmx.remote.port}")
  // private int remotePort;
  
  // read propertie from application.properties file.
  public C3P0ConnectionPoolMetricsScheduler(@Value("${jmx.remote.host}") String remoteHost, @Value("${jmx.remote.port}") int remotePort) {

    this.attributeClient = new JmxMBeanClient(remoteHost, remotePort);

    C3P0GaugeController.createSingletonInstance();
  }

  @Scheduled(fixedRateString = "5000", initialDelayString = "0")
  public void schedulingTask() {

    logger.info("schedulingTask get called, updating metrics");

    C3P0GaugeController.retrieveMetrics(attributeClient);
  }

}
