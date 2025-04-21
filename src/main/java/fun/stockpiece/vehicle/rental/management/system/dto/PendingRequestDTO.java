package fun.stockpiece.vehicle.rental.management.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingRequestDTO {
    private String requestId;
    private ObjectId userId;
    private String username;
    private String email;
    private String fullname;
    private String phoneNumber;
    private String address;
    private Set<User.UserRole> roles;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double depositAmount; // For Customer

    private boolean isEmailVerified;
    private LocalDateTime submittedAt;
}