
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;
import javax.jms.TopicSubscriber;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.command.ActiveMQTopic;

public class Main {

	public static void main(String[] args) throws JMSException {
		
ConnectionFactory factory = new ActiveMQConnectionFactory("tcp://localhost:61616");
		
		Connection connection = factory.createConnection();
		connection.setClientID("cltid");
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		
		
		Topic topic = new ActiveMQTopic("testTopic");
		
		TopicSubscriber receiver = session.createDurableSubscriber(topic, "JMS");
		
			receiver.setMessageListener(new MessageListener() {
				
				public void onMessage(Message message) {
					try {
						System.out.println(((TextMessage)message).getText());
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			});
		connection.start();
	}

}
