import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageSource;
import org.springframework.integration.jms.JmsOutboundGateway;
import org.springframework.integration.jpa.core.JpaExecutor;
import org.springframework.integration.jpa.inbound.JpaPollingChannelAdapter;

@Configuration
@EnableIntegration
public class IntegrationConfig {

	@Autowired
	EntityManagerFactory emf;
	
	
	@InboundChannelAdapter(channel="jpaInboundAdapter", poller=@Poller(fixedDelay="2000"))
	public MessageSource<?> inboundChannel() {
		JpaPollingChannelAdapter ad = new JpaPollingChannelAdapter(jpaExecutor());
		return ad;
	}
	
	private JpaExecutor jpaExecutor() {
		JpaExecutor exec = new JpaExecutor(emf);
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
