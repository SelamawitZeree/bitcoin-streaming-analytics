package cs590.project.order;

import com.amazonaws.serverless.proxy.model.AwsProxyRequest;
import com.amazonaws.serverless.proxy.model.AwsProxyResponse;
import com.amazonaws.serverless.proxy.spring.SpringBootLambdaContainerHandler;
import com.amazonaws.serverless.exceptions.ContainerInitializationException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class OrderHandler implements RequestStreamHandler {
    
    private static SpringBootLambdaContainerHandler<AwsProxyRequest, AwsProxyResponse> handler;
    
    static {
        System.setProperty("server.port", "0");
        try {
            handler = SpringBootLambdaContainerHandler.getAwsProxyHandler(OrderApplication.class);
        } catch (ContainerInitializationException e) {
            throw new RuntimeException("Failed to initialize Lambda container", e);
        }
    }
    
    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) throws IOException {
        handler.proxyStream(inputStream, outputStream, context);
    }
}
