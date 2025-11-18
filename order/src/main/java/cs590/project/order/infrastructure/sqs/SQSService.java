package cs590.project.order.infrastructure.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SQSService {
    
    private final AmazonSQS sqsClient;
    private final String queueUrl;
    private final ObjectMapper objectMapper;
    
    public SQSService(@Value("${aws.sqs.queue.url}") String queueUrl) {
        this.sqsClient = AmazonSQSClientBuilder.defaultClient();
        this.queueUrl = queueUrl;
        this.objectMapper = new ObjectMapper();
    }
    
    public void sendMessage(Object message) {
        try {
            String messageBody = objectMapper.writeValueAsString(message);
            SendMessageRequest request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(messageBody);
            sqsClient.sendMessage(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SQS message", e);
        }
    }
}

