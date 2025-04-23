package fun.stockpiece.vehicle.rental.management.system.dto;

import fun.stockpiece.vehicle.rental.management.system.model.Maintenance;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MaintenanceResponse {
    private String maintenanceId;
    private String vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private double cost;
    private Maintenance.MaintenanceStatus status;
}
