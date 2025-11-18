package cs590.project.order.saga;

import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.service.OrderService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

// Kafka removed - using SQS for async messaging instead
// This class is kept for future saga implementation

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderSagaOrchestrator {

    private final OrderService orderService;

    // Simplified saga - actual implementation would use SQS for async messaging
    // For now, order checkout directly sends to SQS via ShoppingCartService

    // Placeholder event classes for future saga implementation
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ReserveStockEvent {
        private String orderId;
        private java.util.List<cs590.project.order.domain.model.OrderItem> items;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProcessPaymentEvent {
        private String orderId;
        private double amount;
        private String userId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockReservedEvent {
        private String orderId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StockReservationFailedEvent {
        private String orderId;
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentProcessedEvent {
        private String orderId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentFailedEvent {
        private String orderId;
        private String reason;
    }
}
