package fun.stockpiece.vehicle.rental.management.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String id;
    private String username;
    private String fullname;
    private String email;
    private String phoneNumber;
    private String address;
    private Set<User.UserRole> roles;
    private boolean emailVerified;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Double depositAmount; // For Customer
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isApproved;   // For Driver and Customer
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean isAvailable;  // For Driver
}