package fun.stockpiece.vehicle.rental.management.system.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
    @NotBlank(message = "username must be provided")
    private String username;
    @NotBlank(message = "password must be provided")
    private String password;
}

