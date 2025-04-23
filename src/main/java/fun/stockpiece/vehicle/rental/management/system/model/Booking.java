package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    private ObjectId bookingId;
    private ObjectId customerId;
    private ObjectId vehicleId;
    private ObjectId driverId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;
    private BookingStatus status;

    public static enum BookingStatus {
        BOOKED,
        CANCELLED,
        COMPLETED,
        ONGOING
    }
}
