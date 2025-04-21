package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@SuperBuilder
public class Customer extends User {
    private double depositAmount;
    private boolean isApproved;

    public Customer() {
        addRole(UserRole.CUSTOMER);
    }
}
