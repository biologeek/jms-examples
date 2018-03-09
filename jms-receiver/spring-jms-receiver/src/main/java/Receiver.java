import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@Component
public class Receiver {

	
	@JmsListener(containerFactory="jmsListenerContainerFactory", destination = "testTopic")
	public void onMessage(Message message) throws JMSException {
		System.out.println("Received Spring-managed message <"+((TextMessage) message).getText()+">");
	}
}
