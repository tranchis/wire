package eu.superhub.wp4.monitor.wp3servicedata;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.ProducerTemplate;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class JMSServicesUtils implements CamelContextAware {
	private static final Logger LOGGER = Logger
			.getLogger(JMSServicesUtils.class);
	private static CamelContext context;

	@Override
	public void setCamelContext(CamelContext camelContext) {
		LOGGER.info("Running INSIDE container setting global Camel Context");
		context = camelContext;
	}

	@Override
	public CamelContext getCamelContext() {
		return context;
	}

	public static ProducerTemplate getProducerTemplate() {
		if (context == null) {
			LOGGER.info("Running OUTSIDE container gettting aplication context from classpath");
			ApplicationContext appContext = new ClassPathXmlApplicationContext(
					"camel-client.xml");
			return appContext.getBean("camelTemplate", ProducerTemplate.class);
		}
		return context.createProducerTemplate();
	}

}
