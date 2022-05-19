package dev.kyma.samples.easyfranchise.day2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import io.prometheus.client.exporter.MetricsServlet;

@SpringBootApplication
@EnableScheduling
public class Day2ServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(Day2ServiceApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean<MetricsServlet> servletRegistrationBean() {
		//DefaultExports.initialize();
		return new ServletRegistrationBean<>(new MetricsServlet(), "/prometheus/metrics");
	}	
}
