package cs590.project.product.domain;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private String productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private String description;

    public Product() {
    }

    public Product(String productId, String name, Integer quantity, BigDecimal price, String description) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(productId, product.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
               "productId='" + productId + '\'' +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               ", description='" + description + '\'' +
               '}';
    }
}
