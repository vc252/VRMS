package fun.stockpiece.vehicle.rental.management.system.dto;

import fun.stockpiece.vehicle.rental.management.system.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleResponse {
    private String id;
    private String registrationNumber;
    private Vehicle.Category vehicleType;
    private String vehicleName;
    private int seatingCapacity;
    private Vehicle.FuelType fuelType;
    private double perHourRate;
    private double perKilometerRate;
    private int sellingPrice;
    private boolean hasAC;
    private boolean premium;
    private Vehicle.VehicleState status;
    private LocalDateTime addedOn;
}