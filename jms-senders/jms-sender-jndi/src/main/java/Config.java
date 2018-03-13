import java.util.Properties;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;

@Configuration
@EnableJms
public class Config {
	
	@Bean
	public JndiObjectFactoryBean jndiConnectionFactory() {
		JndiObjectFactoryBean bean = new JndiObjectFactoryBean();
		bean.setProxyInterface(ConnectionFactory.class);
		bean.setJndiName("ConnectionFactory");
		bean.setJndiTemplate(jndiTemplate());
		return bean;
	}

	@Bean
	public JndiTemplate jndiTemplate() {
		JndiTemplate tpl = new JndiTemplate();
		tpl.setEnvironment(environment());
		return tpl;
	}

	private Properties environment() {
		Properties props = new Properties();
		props.put("java.naming.factory.initial", "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		props.put("java.naming.provider.url", "tcp://localhost:61616");
		props.put("java.naming.factory.url.pkgs", "");
		return props;
	}
	
	@Bean
	public JmsTemplate jmsTemplate(ConnectionFactory factory) {
		JmsTemplate tpl = new JmsTemplate();
		tpl.setConnectionFactory(factory);
		tpl.setDefaultDestinationName("testTopic");
		tpl.setPubSubDomain(true);
		
		return tpl;
	}
	
	@Bean
	public Session session(ConnectionFactory factory) throws JMSException {
		return factory.createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
}
