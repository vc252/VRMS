package fun.stockpiece.vehicle.rental.management.system.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PendingRequestDTO {
    private String requestId;
    private String userId;
    private String username;
    private String email;
    private String fullname;
    private String phoneNumber;
    private String address;
    private boolean isEmailVerified;
    private LocalDateTime submittedAt;
}