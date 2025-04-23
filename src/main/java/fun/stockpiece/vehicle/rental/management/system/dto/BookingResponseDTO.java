package fun.stockpiece.vehicle.rental.management.system.dto;

import fun.stockpiece.vehicle.rental.management.system.model.Booking.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private String bookingId;
    private String customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String destination;
    private BookingStatus status;

    // Driver details
    private String driverId;
    private String driverName;
    private String driverPhone;

    // Vehicle details
    private String vehicleId;
    private String vehicleName;
    private String registrationNumber;
}