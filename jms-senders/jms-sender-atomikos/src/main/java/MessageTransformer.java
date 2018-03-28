import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

@MessageEndpoint
public class MessageTransformer {
	
	@Transformer(inputChannel="jpaInboundAdapter", outputChannel="afterTransformChannel")
	public Message<?> transform(Message<?> input){
		System.out.println("Processing message "+ input.getPayload());
		
		if (input.getPayload() != null)
			((io.biologeek.Message) input.getPayload()).setStatus(1);
		return input;		
	}

}
