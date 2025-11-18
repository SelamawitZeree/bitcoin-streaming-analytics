package cs590.project.product.sqs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs590.project.product.service.ProductService;
import io.awspring.cloud.sqs.annotation.SqsListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StockListener {

    private static final Logger log = LoggerFactory.getLogger(StockListener.class);
    private final ProductService productService;
    private final ObjectMapper objectMapper;

    public StockListener(ProductService productService, ObjectMapper objectMapper) {
        this.productService = productService;
        this.objectMapper = objectMapper;
    }

    @SqsListener("${cloud.aws.sqs.queue.stock-decrement}")
    public void listenToStockDecrementQueue(String message) {
        try {
            log.info("Received SQS message: {}", message);
            StockDecrementMessage stockDecrementMessage = objectMapper.readValue(message, StockDecrementMessage.class);
            productService.decrementStock(stockDecrementMessage.getProductId(), stockDecrementMessage.getQuantity());
            log.info("Successfully decremented stock for product ID: {}", stockDecrementMessage.getProductId());
        } catch (JsonProcessingException e) {
            log.error("Error parsing SQS message: {}", message, e);
            // Depending on requirements, you might want to dead-letter the message or retry
        } catch (IllegalArgumentException e) {
            log.error("Error processing stock decrement: {}", e.getMessage());
            // Handle cases like product not found or insufficient stock
        } catch (Exception e) {
            log.error("An unexpected error occurred while processing SQS message: {}", message, e);
        }
    }
}
