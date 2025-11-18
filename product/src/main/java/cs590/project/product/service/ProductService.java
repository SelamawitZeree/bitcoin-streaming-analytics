package cs590.project.product.service;

import cs590.project.product.domain.Product;
import cs590.project.product.infrastructure.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product saveProduct(Product product) {
        // Additional business logic for saving/updating could go here
        return productRepository.save(product);
    }

    public Optional<Product> getProduct(String productId) {
        return productRepository.findById(productId);
    }

    public void deleteProduct(String productId) {
        productRepository.deleteById(productId);
    }

    public Product updateStock(String productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> {
                    product.setQuantity(quantity);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
    }

    public Product decrementStock(String productId, int quantity) {
        return productRepository.findById(productId)
                .map(product -> {
                    if (product.getQuantity() < quantity) {
                        throw new IllegalArgumentException("Insufficient stock for product ID: " + productId);
                    }
                    product.setQuantity(product.getQuantity() - quantity);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));
    }

    public List<Product> searchProductsByName(String name) {
        return productRepository.findByName(name);
    }
}
