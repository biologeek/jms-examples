import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jms.JmsOutboundGateway;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
public class IntegrationConfig {

	@PersistenceContext
	EntityManager em;

	@Bean
	public MessageChannel jpaInboundAdapterChannel() {
		return new DirectChannel();
	}
	
	@Bean
	public MessageChannel afterTransformChannel() {
		return new DirectChannel();
	}
	
	@InboundChannelAdapter(channel="jpaInboundAdapterChannel", poller=@Poller(fixedDelay="2000"))
	@Bean
	public MessageSource<?> inboundChannel() {
		JpaPollingChannelAdapter ad = new JpaPollingChannelAdapter(jpaExecutor());
		return ad;
	}
	
	@Bean
	public JpaExecutor jpaExecutor() {
		JpaExecutor exec = new JpaExecutor(em);
		exec.setJpaQuery("select a from Message a where status = 0");
		exec.setMaxNumberOfResults(10);
		return exec;
	}
	
	
	
	@Bean
	@ServiceActivator(inputChannel="afterTransformChannel")
	public JmsOutboundGateway jmsGateway(ConnectionFactory jmsFactory, Destination dest) {
		JmsOutboundGateway g = new JmsOutboundGateway();
		g.setConnectionFactory(jmsFactory);
		g.setRequestDestination(dest);
		
		return g;
	}
	
}
