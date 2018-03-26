import javax.jms.Destination;
import javax.persistence.EntityManagerFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.jms.Jms;
import org.springframework.integration.dsl.jpa.Jpa;
import org.springframework.integration.transformer.GenericTransformer;
import org.springframework.jms.core.JmsTemplate;

import io.biologeek.Message;

@Configuration
@EnableIntegration
public class IntegrationConfig {

	
	@Bean
	public IntegrationFlow dbToJmsFlow(EntityManagerFactory emf, JmsTemplate template, Destination topic) {
		return IntegrationFlows.from(Jpa.inboundAdapter(emf).jpaQuery("select a from Message a where status = 0")//
				.maxResults(10))//
				.transform(new GenericTransformer<Message, Message>() {

					public Message transform(Message source) {
						source.setStatus(1);
						return source;
					}
				}).handle(Jms.outboundAdapter(template)//
						.destination(topic)).get();
	}
	
	
	
}
