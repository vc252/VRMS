package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "vehicle")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Vehicle {
    @Id
    private ObjectId vehicleId;
    @Indexed(unique = true)
    private String registrationNumber;
    private Category vehicleType;
    private String vehicleName;//car brand name
    private int seatingCapacity;
    private FuelType fuelType;
    private VehicleState state;
    private double perHourRate;
    private double perKilometerRate;
    private int sellingPrice;
    private boolean hasAC;
    private boolean premium;
    private LocalDateTime addedOn;

    public static enum Category {
        SUV,
        SEDAN,
        HATCHBACK,
        TRUCK,
        VAN,
        TRAVELLER,
        BUS,
        LIMOUSINE,
        CONVERTIBLE
    }

    public static enum FuelType {
        DIESEL,
        PETROL,
        CNG,
        ELECTRIC
    }

    public static enum VehicleState {
        AVAILABLE,
        REPAIR,
        RENTED
    }
}
