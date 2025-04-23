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
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Document(collection = "dutyslip")
public class DutySlip {
    @Id
    private ObjectId dutySlipId;
    private ObjectId bookingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int startOdometer;
    private int endOdometer;
    private int nightHalt;
    private String comments;
}
