package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@EqualsAndHashCode(callSuper = true)
@Document(collection = "users")
@Data
@SuperBuilder
public class Driver extends User{
    private boolean isApproved;
    private boolean isAvailable;

    public Driver() {
        addRole(UserRole.DRIVER);
    }
}
