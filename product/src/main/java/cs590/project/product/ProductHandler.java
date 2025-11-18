package cs590.project.product;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import cs590.project.product.sqs.StockListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProductHandler implements RequestStreamHandler {
    
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> apiHandler;
    private static ConfigurableApplicationContext sqsContext;
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Object lock = new Object();
    
    static {
        System.setProperty("server.port", "0");
        try {
            apiHandler = SpringBootLambdaContainerHandler.getAwsProxyHandler(ProductApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("Failed to initialize Lambda container", e);
        }
    }
    
    private static ConfigurableApplicationContext getSQSContext() {
        if (sqsContext == null) {
            synchronized (lock) {
                if (sqsContext == null) {
                    sqsContext = (ConfigurableApplicationContext) SpringApplication.run(ProductApplication.class);
                }
            }
        }
        return sqsContext;
    }
    
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        try {
            byte[] inputBytes = inputStream.readAllBytes();
            String input = new String(inputBytes);
            
            if (input.trim().startsWith("{")) {
                try {
                    SQSEvent sqsEvent = objectMapper.readValue(input, SQSEvent.class);
                    if (sqsEvent.getRecords() != null && !sqsEvent.getRecords().isEmpty()) {
                        handleSQS(sqsEvent, context);
                        return;
                    }
                } catch (Exception e) {
                }
            }
            
            apiHandler.proxyStream(new java.io.ByteArrayInputStream(inputBytes), outputStream, context);
        } catch (Exception e) {
            context.getLogger().log("Error: " + e.getMessage());
            throw new IOException(e);
        }
    }
    
    private void handleSQS(SQSEvent event, Context context) {
        context.getLogger().log("Processing SQS event with " + event.getRecords().size() + " records");
        ConfigurableApplicationContext ctx = getSQSContext();
        StockListener stockListener = ctx.getBean(StockListener.class);
        
        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                context.getLogger().log("Processing message: " + message.getBody());
                stockListener.listenToStockDecrementQueue(message.getBody());
            } catch (Exception e) {
                context.getLogger().log("Error processing SQS message: " + e.getMessage());
            }
        }
    }
}
