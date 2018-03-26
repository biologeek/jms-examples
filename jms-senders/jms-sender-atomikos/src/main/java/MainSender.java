import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

@Transactional
public class MainSender {

	public static void main(String[] args) {
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeansConfig.class);
		//ctx.refresh();
		JmsTemplate tpl = ctx.getBean(JmsTemplate.class);

		final EntityManager em = ctx.getBean(EntityManagerFactory.class).createEntityManager();
		
		em.persist(new io.biologeek.Message(1L, "Coucou Spring Atomikos"));
		tpl.send(new MessageCreator() {

			public Message createMessage(Session session) throws JMSException {
				return ctx.getBean(Session.class).createTextMessage(em.find(io.biologeek.Message.class, 1L).getText());
			}
		});
		
		System.exit(0);
	}
}
