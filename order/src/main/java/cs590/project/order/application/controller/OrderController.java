package cs590.project.order.application.controller;

import cs590.project.order.application.dto.OrderDto;
import cs590.project.order.application.dto.OrderDtoMapper;
import cs590.project.order.domain.model.Order;
import cs590.project.order.domain.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderDtoMapper orderDtoMapper;

    @Operation(summary = "Get details of a specific order by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrderById(@PathVariable String orderId) {
        return orderService.getOrderById(orderId)
                .map(orderDtoMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get a user's order history by user ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order history retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found or no orders for user")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderDto>> getOrdersForUser(@PathVariable String userId) {
        List<Order> orders = orderService.getOrdersForUser(userId);
        if (orders.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        List<OrderDto> orderDtos = orders.stream()
                .map(orderDtoMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDtos);
    }
}
