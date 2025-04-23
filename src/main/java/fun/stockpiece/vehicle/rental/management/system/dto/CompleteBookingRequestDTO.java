package fun.stockpiece.vehicle.rental.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CompleteBookingRequestDTO {
    private int endOdometer;
    private int nightHalt;
    private String comments;
}