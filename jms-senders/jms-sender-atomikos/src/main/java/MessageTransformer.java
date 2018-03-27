import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;

public class MessageTransformer {
	
	@Transformer(inputChannel="jpaInboundAdapter", outputChannel="afterTransformChannel")
	public Message<?> transform(Message<?> input){
		if (input.getPayload() != null)
			((io.biologeek.Message) input.getPayload()).setStatus(1);
		return input;		
	}

}
