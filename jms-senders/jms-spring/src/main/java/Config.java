import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;

@Configuration
@EnableJms
public class Config {
	
	@Bean
	public JmsTemplate template() {
		JmsTemplate tpl = new JmsTemplate();
		tpl.setConnectionFactory(activeMQConnectionFactory());		
		//tpl.setPubSubDomain(true);
		tpl.setDefaultDestination(topic());
		
		return tpl;
	}
	@Bean
	public Destination topic() {
		return new ActiveMQTopic("testTopic");
	}

	public ConnectionFactory facto() {
		SingleConnectionFactory factory = new SingleConnectionFactory();
		
		factory.setTargetConnectionFactory(activeMQConnectionFactory());
		
		return factory; 
	}
	

	@Bean
	public ConnectionFactory activeMQConnectionFactory() {
		return new ActiveMQConnectionFactory("tcp://localhost:61616");
	}

	@Bean
	public Session session() throws JMSException {
		return activeMQConnectionFactory().createConnection().createSession(false, Session.AUTO_ACKNOWLEDGE);
	}
}
