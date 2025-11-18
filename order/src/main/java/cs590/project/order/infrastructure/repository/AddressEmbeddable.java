package cs590.project.order.infrastructure.repository;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressEmbeddable {
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
}
