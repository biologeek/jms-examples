import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.DestinationResolver;
import org.springframework.jms.support.destination.DynamicDestinationResolver;

@Configuration
@EnableJms
public class Config {
	
	@Bean
	public Destination topic() {
		return new ActiveMQTopic("testTopic");
	}

	@Bean
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
	
	@Bean
	public DefaultJmsListenerContainerFactory jmsListenerContainerFactory() {
		DefaultJmsListenerContainerFactory listenerFactory = new DefaultJmsListenerContainerFactory();
		
		listenerFactory.setConnectionFactory(facto());
		listenerFactory.setDestinationResolver(destinationResolver());
		listenerFactory.setPubSubDomain(true);
		return listenerFactory;
	}
	@Bean
	public DestinationResolver destinationResolver() {
		return new DynamicDestinationResolver();
	}
}
