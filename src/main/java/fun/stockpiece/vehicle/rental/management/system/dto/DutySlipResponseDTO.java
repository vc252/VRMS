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
public class DutySlipResponseDTO {
    private String dutySlipId;
    private String bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int startOdometer;
    private int endOdometer;
    private int nightHalt;
    private String comments;
}