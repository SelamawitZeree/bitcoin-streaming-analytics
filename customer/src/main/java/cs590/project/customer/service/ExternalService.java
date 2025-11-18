package cs590.project.customer.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalService {

    private final RestTemplate restTemplate;

    public ExternalService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "externalService", fallbackMethod = "fallback")
    public String callExternalService(String url) {
        return restTemplate.getForObject(url, String.class);
    }

    public String fallback(String url, Exception e) {
        return "{\"message\":\"Service unavailable - circuit breaker open\",\"error\":\"" + e.getMessage() + "\"}";
    }
}

