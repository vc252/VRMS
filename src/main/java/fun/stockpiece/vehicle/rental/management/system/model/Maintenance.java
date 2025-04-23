package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "maintenance")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Maintenance {
    @Id
    private ObjectId maintenanceId;
    private ObjectId vehicleId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String description;
    private double cost;
    private MaintenanceStatus status;

    public static enum MaintenanceStatus {
        ONGOING,
        COMPLETED
    }
}
