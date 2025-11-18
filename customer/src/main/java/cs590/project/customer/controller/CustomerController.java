package cs590.project.customer.controller;

import cs590.project.customer.service.ExternalService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class CustomerController {

    private final ExternalService externalService;

    public CustomerController(ExternalService externalService) {
        this.externalService = externalService;
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("userId", jwt.getSubject());
        profile.put("email", jwt.getClaimAsString("email"));
        profile.put("message", "Welcome! You are authenticated.");
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        return ResponseEntity.ok(response);
    }

    @CircuitBreaker(name = "externalService")
    @GetMapping("/external")
    public ResponseEntity<String> callExternal() {
        String result = externalService.callExternalService("http://localhost:8081/stock/");
        return ResponseEntity.ok(result);
    }
}

