package fun.stockpiece.vehicle.rental.management.system.dto;

import fun.stockpiece.vehicle.rental.management.system.model.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {
    private String registrationNumber;
    private Vehicle.Category vehicleType;
    private String vehicleName;//car brand name
    private int seatingCapacity;
    private Vehicle.FuelType fuelType;
    private double perHourRate;
    private double perKilometerRate;
    private int sellingPrice;
    private boolean hasAC;
    private boolean premium;
}
