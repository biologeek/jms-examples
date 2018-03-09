import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MainSpringReceiver {

	
	public static void main(String[] args) {
		
		AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
		ctx.register(Config.class, Receiver.class);
		ctx.refresh();
	}
}
