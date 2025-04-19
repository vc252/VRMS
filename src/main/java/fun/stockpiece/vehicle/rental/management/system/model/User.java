package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Document(collection = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class User {
    @Id
    private ObjectId userId;
    @Indexed(unique = true)
    private String username;
    private String fullname;
    private String password;
    @Indexed(unique = true)
    private String email;
    @Builder.Default
    private boolean isEmailVerified = false;
    private String phoneNumber;
    private String address;
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    @Builder.Default
    private Set<UserRole> roles = new HashSet<>();

    public void addRole(UserRole role) {
        roles.add(role);
    }

    public boolean isProfileComplete() {
        return isEmailVerified && phoneNumber != null && !phoneNumber.isEmpty() && address != null && !address.isEmpty();
    }

    public static enum UserRole {
        ADMIN,
        SUPER_ADMIN,
        CUSTOMER,
        DRIVER
    }
}

