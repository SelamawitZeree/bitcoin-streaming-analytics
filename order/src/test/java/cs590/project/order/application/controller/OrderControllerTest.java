package cs590.project.order.application.controller;

import cs590.project.order.domain.model.Address;
import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.model.OrderItem;
import cs590.project.order.domain.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    private Order testOrder;
    private String testOrderId;
    private String testUserId;

    @BeforeEach
    void setUp() {
        testOrderId = UUID.randomUUID().toString();
        testUserId = "user123";

        OrderItem item1 = new OrderItem("prod1", "Product 1", 10.0, 2);
        List<OrderItem> orderItems = Collections.singletonList(item1);

        Address shippingAddress = new Address("123 Main St", "Anytown", "CA", "90210", "USA");
        Address billingAddress = new Address("123 Main St", "Anytown", "CA", "90210", "USA");

        testOrder = new Order(
                testOrderId,
                testUserId,
                orderItems,
                20.0,
                Order.OrderStatus.PENDING,
                shippingAddress,
                billingAddress,
                LocalDateTime.now()
        );
    }

    @Test
    void getOrderById_shouldReturnOrderDetails() throws Exception {
        when(orderService.getOrderById(testOrderId)).thenReturn(Optional.of(testOrder));

        mockMvc.perform(get("/orders/{orderId}", testOrderId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(testOrderId))
                .andExpect(jsonPath("$.userId").value(testUserId))
                .andExpect(jsonPath("$.totalPrice").value(20.0))
                .andExpect(jsonPath("$.orderStatus").value(Order.OrderStatus.PENDING.name()))
                .andExpect(jsonPath("$.orderItems[0].productId").value("prod1"));
    }

    @Test
    void getOrderById_shouldReturnNotFoundIfOrderDoesNotExist() throws Exception {
        when(orderService.getOrderById("nonExistentId")).thenReturn(Optional.empty());

        mockMvc.perform(get("/orders/{orderId}", "nonExistentId")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void getOrdersForUser_shouldReturnListOfOrders() throws Exception {
        List<Order> userOrders = Arrays.asList(testOrder);
        when(orderService.getOrdersForUser(testUserId)).thenReturn(userOrders);

        mockMvc.perform(get("/orders/user/{userId}", testUserId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(testOrderId))
                .andExpect(jsonPath("$[0].userId").value(testUserId));
    }

    @Test
    void getOrdersForUser_shouldReturnNotFoundIfNoOrdersForUser() throws Exception {
        when(orderService.getOrdersForUser("nonExistentUser")).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/orders/user/{userId}", "nonExistentUser")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
