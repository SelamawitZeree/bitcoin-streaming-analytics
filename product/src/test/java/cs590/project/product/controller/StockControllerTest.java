package cs590.project.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import cs590.project.product.domain.Product;
import cs590.project.product.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StockController.class)
class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void addOrUpdateProduct_shouldReturnSavedProduct_whenValid() throws Exception {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productService.saveProduct(any(Product.class))).thenReturn(product);

        mockMvc.perform(post("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("1"))
                .andExpect(jsonPath("$.name").value("Test Product"));
    }

    @Test
    void addOrUpdateProduct_shouldReturnBadRequest_whenInvalid() throws Exception {
        Product product = new Product(null, null, 10, BigDecimal.valueOf(100.00), "Description"); // Invalid product
        mockMvc.perform(post("/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getProductById_shouldReturnProduct_whenFound() throws Exception {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productService.getProduct("1")).thenReturn(Optional.of(product));

        mockMvc.perform(get("/stock/{productId}", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("1"));
    }

    @Test
    void getProductById_shouldReturnNotFound_whenNotFound() throws Exception {
        when(productService.getProduct("1")).thenReturn(Optional.empty());

        mockMvc.perform(get("/stock/{productId}", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateProductStock_shouldReturnUpdatedProduct_whenValid() throws Exception {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productService.updateStock(eq("1"), eq(15))).thenReturn(product);

        mockMvc.perform(put("/stock/{productId}", "1")
                        .param("quantity", "15"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productId").value("1"));
    }

    @Test
    void updateProductStock_shouldReturnBadRequest_whenProductNotFound() throws Exception {
        when(productService.updateStock(eq("1"), anyInt()))
                .thenThrow(new IllegalArgumentException("Product not found"));

        mockMvc.perform(put("/stock/{productId}", "1")
                        .param("quantity", "15"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void searchProductsByName_shouldReturnProducts_whenFound() throws Exception {
        Product product1 = new Product("1", "Laptop", 10, BigDecimal.valueOf(1200.00), "Gaming Laptop");
        Product product2 = new Product("2", "Laptop Pro", 5, BigDecimal.valueOf(1500.00), "Workstation Laptop");
        when(productService.searchProductsByName("Laptop")).thenReturn(Arrays.asList(product1, product2));

        mockMvc.perform(get("/stock/search")
                        .param("name", "Laptop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Laptop Pro"));
    }

    @Test
    void searchProductsByName_shouldReturnNoContent_whenNotFound() throws Exception {
        when(productService.searchProductsByName("NonExistent")).thenReturn(Arrays.asList());

        mockMvc.perform(get("/stock/search")
                        .param("name", "NonExistent"))
                .andExpect(status().isNoContent());
    }
}
