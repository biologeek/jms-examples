import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

public class MainSender {

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
		//ctx.refresh();
		JmsTemplate tpl = ctx.getBean(JmsTemplate.class);

		tpl.send(new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				
				return ctx.getBean(Session.class).createTextMessage("Coucou SPRING");
			}
		});
		
		System.exit(0);
	}
}
