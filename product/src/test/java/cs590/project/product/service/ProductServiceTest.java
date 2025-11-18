package cs590.project.product.service;

import cs590.project.product.domain.Product;
import cs590.project.product.infrastructure.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveProduct_shouldReturnSavedProduct() {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product savedProduct = productService.saveProduct(product);

        assertNotNull(savedProduct);
        assertEquals("1", savedProduct.getProductId());
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void getProduct_shouldReturnProduct_whenFound() {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        Optional<Product> foundProduct = productService.getProduct("1");

        assertTrue(foundProduct.isPresent());
        assertEquals("1", foundProduct.get().getProductId());
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void getProduct_shouldReturnEmpty_whenNotFound() {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        Optional<Product> foundProduct = productService.getProduct("1");

        assertFalse(foundProduct.isPresent());
        verify(productRepository, times(1)).findById("1");
    }

    @Test
    void deleteProduct_shouldCallRepositoryDelete() {
        productService.deleteProduct("1");
        verify(productRepository, times(1)).deleteById("1");
    }

    @Test
    void updateStock_shouldUpdateQuantity_whenProductFound() {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product updatedProduct = productService.updateStock("1", 15);

        assertNotNull(updatedProduct);
        assertEquals(15, updatedProduct.getQuantity());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void updateStock_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.updateStock("1", 15);
        });

        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void decrementStock_shouldDecrementQuantity_whenSufficientStock() {
        Product product = new Product("1", "Test Product", 10, BigDecimal.valueOf(100.00), "Description");
        when(productRepository.findById("1")).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product decrementedProduct = productService.decrementStock("1", 3);

        assertNotNull(decrementedProduct);
        assertEquals(7, decrementedProduct.getQuantity());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, times(1)).save(product);
    }

    @Test
    void decrementStock_shouldThrowException_whenInsufficientStock() {
        Product product = new Product("1", "Test Product", 5, BigDecimal.valueOf(100.00), "Description");
        when(productRepository.findById("1")).thenReturn(Optional.of(product));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.decrementStock("1", 10);
        });

        assertEquals("Insufficient stock for product ID: 1", exception.getMessage());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void decrementStock_shouldThrowException_whenProductNotFound() {
        when(productRepository.findById("1")).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.decrementStock("1", 5);
        });

        assertEquals("Product not found with ID: 1", exception.getMessage());
        verify(productRepository, times(1)).findById("1");
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    void searchProductsByName_shouldReturnMatchingProducts() {
        Product product1 = new Product("1", "Laptop", 10, BigDecimal.valueOf(1200.00), "Gaming Laptop");
        Product product2 = new Product("2", "Laptop Pro", 5, BigDecimal.valueOf(1500.00), "Workstation Laptop");
        List<Product> products = Arrays.asList(product1, product2);
        when(productRepository.findByName("Laptop")).thenReturn(products);

        List<Product> foundProducts = productService.searchProductsByName("Laptop");

        assertNotNull(foundProducts);
        assertEquals(2, foundProducts.size());
        assertTrue(foundProducts.contains(product1));
        assertTrue(foundProducts.contains(product2));
        verify(productRepository, times(1)).findByName("Laptop");
    }

    @Test
    void searchProductsByName_shouldReturnEmptyList_whenNoMatch() {
        when(productRepository.findByName("NonExistent")).thenReturn(Arrays.asList());

        List<Product> foundProducts = productService.searchProductsByName("NonExistent");

        assertNotNull(foundProducts);
        assertTrue(foundProducts.isEmpty());
        verify(productRepository, times(1)).findByName("NonExistent");
    }
}
