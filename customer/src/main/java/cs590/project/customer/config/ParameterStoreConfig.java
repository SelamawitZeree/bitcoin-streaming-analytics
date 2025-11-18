package cs590.project.customer.config;

import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagement;
import com.amazonaws.services.simplesystemsmanagement.AWSSimpleSystemsManagementClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ParameterStoreConfig {

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Bean
    public AWSSimpleSystemsManagement ssmClient() {
        return AWSSimpleSystemsManagementClientBuilder.standard()
                .withRegion(awsRegion)
                .build();
    }
}

