package fun.stockpiece.vehicle.rental.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private String vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;
}