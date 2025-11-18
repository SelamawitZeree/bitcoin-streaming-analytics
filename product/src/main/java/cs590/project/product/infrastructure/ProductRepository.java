package cs590.project.product.infrastructure;

import cs590.project.product.domain.Product;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class ProductRepository {

    private final DynamoDBMapper dynamoDBMapper;

    public ProductRepository(DynamoDBMapper dynamoDBMapper) {
        this.dynamoDBMapper = dynamoDBMapper;
    }

    public Product save(Product product) {
        ProductDynamoDbEntity entity = toDynamoDbEntity(product);
        dynamoDBMapper.save(entity);
        return toDomain(entity);
    }

    public Optional<Product> findById(String productId) {
        ProductDynamoDbEntity entity = dynamoDBMapper.load(ProductDynamoDbEntity.class, productId);
        return Optional.ofNullable(entity).map(this::toDomain);
    }

    public void deleteById(String productId) {
        ProductDynamoDbEntity entity = dynamoDBMapper.load(ProductDynamoDbEntity.class, productId);
        if (entity != null) {
            dynamoDBMapper.delete(entity);
        }
    }

    public List<Product> findByName(String name) {
        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":name", new AttributeValue().withS(name));

        DynamoDBQueryExpression<ProductDynamoDbEntity> queryExpression = new DynamoDBQueryExpression<ProductDynamoDbEntity>()
                .withIndexName("name-index") // Assuming a GSI named "name-index" on the 'name' attribute
                .withKeyConditionExpression("name = :name")
                .withExpressionAttributeValues(expressionAttributeValues)
                .withConsistentRead(false); // GSI queries are eventually consistent

        List<ProductDynamoDbEntity> entities = dynamoDBMapper.query(ProductDynamoDbEntity.class, queryExpression);
        return entities.stream().map(this::toDomain).collect(Collectors.toList());
    }

    private ProductDynamoDbEntity toDynamoDbEntity(Product product) {
        ProductDynamoDbEntity entity = new ProductDynamoDbEntity();
        entity.setProductId(product.getProductId());
        entity.setName(product.getName());
        entity.setQuantity(product.getQuantity());
        entity.setPrice(product.getPrice());
        entity.setDescription(product.getDescription());
        return entity;
    }

    private Product toDomain(ProductDynamoDbEntity entity) {
        return new Product(
                entity.getProductId(),
                entity.getName(),
                entity.getQuantity(),
                entity.getPrice(),
                entity.getDescription()
        );
    }
}
