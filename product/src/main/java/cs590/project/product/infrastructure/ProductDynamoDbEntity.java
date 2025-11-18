package cs590.project.product.infrastructure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import java.math.BigDecimal;
import java.util.Objects;

@DynamoDBTable(tableName = "Products")
public class ProductDynamoDbEntity {

    private String productId;
    private String name;
    private Integer quantity;
    private BigDecimal price;
    private String description;

    @DynamoDBHashKey(attributeName = "productId")
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @DynamoDBAttribute(attributeName = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @DynamoDBAttribute(attributeName = "quantity")
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @DynamoDBAttribute(attributeName = "price")
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @DynamoDBAttribute(attributeName = "description")
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
        ProductDynamoDbEntity that = (ProductDynamoDbEntity) o;
        return Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return "ProductDynamoDbEntity{" +
               "productId='" + productId + '\'' +
               ", name='" + name + '\'' +
               ", quantity=" + quantity +
               ", price=" + price +
               ", description='" + description + '\'' +
               '}';
    }
}
