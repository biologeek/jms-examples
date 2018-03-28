import java.io.IOException;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Transactional
public class MainSender {

	public static void main(String[] args) throws IOException {
		final AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(BeansConfig.class, IntegrationConfig.class);
		
		EntityManager em = ctx.getBean(EntityManager.class);

		System.out.println("Inserting 2 lines ! ");
		em.createNativeQuery("INSERT INTO message VALUES (1, 'My text', 0)");
		em.createNativeQuery("INSERT INTO message VALUES (2, 'My second text', 0)");
		
		System.in.read();
		ctx.close();
		System.exit(0);
	}
}
