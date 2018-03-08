import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnectionFactory;

public class Main {
	
	
	public static void main(String[] args) throws JMSException {
		ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		
		Connection connection = factory.createConnection();
		connection.setClientID("othercltid");
		
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		
		Topic topic = session.createTopic("testTopic");
		MessageProducer publisher = session.createProducer(topic);
		
		
		TextMessage msg = session.createTextMessage();
		msg.setText("Coucou");
		
		try {
			publisher.send(msg);
		}
		catch (Exception e) {
		
		}
		finally {
			connection.close();
		}
	}

}
