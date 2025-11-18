package cs590.project.product.controller;

import cs590.project.product.domain.Product;
import cs590.project.product.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/stock")
public class StockController {

    private final ProductService productService;

    public StockController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Add a new product or update an existing one")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product added/updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid product details supplied",
                    content = @Content)
    })
    @PostMapping
    public ResponseEntity<Product> addOrUpdateProduct(@RequestBody Product product) {
        if (product.getProductId() == null || product.getName() == null || product.getQuantity() == null || product.getPrice() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Product savedProduct = productService.saveProduct(product);
        return new ResponseEntity<>(savedProduct, HttpStatus.OK);
    }

    @Operation(summary = "Get a product by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the product",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "404", description = "Product not found",
                    content = @Content)
    })
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(
            @Parameter(description = "ID of the product to be retrieved", required = true)
            @PathVariable String productId) {
        return productService.getProduct(productId)
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Update the stock quantity of a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock updated successfully",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid quantity or product not found",
                    content = @Content)
    })
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProductStock(
            @Parameter(description = "ID of the product to update stock for", required = true)
            @PathVariable String productId,
            @Parameter(description = "New quantity for the product stock", required = true)
            @RequestParam int quantity) {
        try {
            Product updatedProduct = productService.updateStock(productId, quantity);
            return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "Search for products by name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found products matching the name",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Product.class))}),
            @ApiResponse(responseCode = "204", description = "No products found for the given name",
                    content = @Content)
    })
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(
            @Parameter(description = "Name to search for", required = true)
            @RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        if (products.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(products, HttpStatus.OK);
    }
}
