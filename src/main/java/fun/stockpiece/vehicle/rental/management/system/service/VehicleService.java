package fun.stockpiece.vehicle.rental.management.system.service;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
import fun.stockpiece.vehicle.rental.management.system.dto.MaintenanceResponse;
import fun.stockpiece.vehicle.rental.management.system.dto.VehicleResponse;
import fun.stockpiece.vehicle.rental.management.system.model.Maintenance;
import fun.stockpiece.vehicle.rental.management.system.model.Vehicle;
import fun.stockpiece.vehicle.rental.management.system.repository.MaintenanceRepository;
import fun.stockpiece.vehicle.rental.management.system.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VehicleService {
    private final VehicleRepository vehicleRepository;
    private final MaintenanceRepository maintenanceRepository;

    public VehicleResponse addVehicle(Vehicle vehicle) {
        if (vehicle.getState() == null) {
            vehicle.setState(Vehicle.VehicleState.AVAILABLE);
        }
        vehicle.setAddedOn(LocalDateTime.now());
        return vehicleToVehicleResponse(vehicleRepository.save(vehicle));
    }

    public int sellVehicle(String vehicleId) {
        try {
            ObjectId id = new ObjectId(vehicleId);
            Vehicle vehicle = vehicleRepository.findById(id)
                    .orElseThrow(() -> new ApiException(
                            "Invalid Delete request",
                            HttpStatus.BAD_REQUEST.value(),
                            "Vehicle not found with id: " + vehicleId
                    ));
            if (vehicle.getState() == Vehicle.VehicleState.RENTED) {
                throw new ApiException("Invalid Delete request",HttpStatus.CONFLICT.value(), "cannot delete a rented vehicle");
            }
            vehicleRepository.deleteById(id);
            return vehicle.getSellingPrice();
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    "invalid object id",
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid vehicle ID format: " + vehicleId
            );
        }
    }

    public MaintenanceResponse sendVehicleForRepair(String vehicleId) {
        try {
            ObjectId id = new ObjectId(vehicleId);
            Vehicle vehicle = vehicleRepository.findById(id)
                    .orElseThrow(() -> new ApiException(
                            "Invalid Delete request",
                            HttpStatus.BAD_REQUEST.value(),
                            "Vehicle not found with id: " + vehicleId));
            if (vehicle.getState() == Vehicle.VehicleState.RENTED) {
                throw new IllegalStateException("Cannot send rented vehicle for repair");
            }
            vehicle.setState(Vehicle.VehicleState.REPAIR);
            vehicleRepository.save(vehicle);

            Maintenance maintenance = Maintenance.builder()
                    .vehicleId(id)
                    .status(Maintenance.MaintenanceStatus.ONGOING)
                    .startDate(LocalDateTime.now())
                    .build();

            return maintenanceToMaintenanceResponse(maintenanceRepository.save(maintenance));
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    "invalid object id",
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid vehicle ID format: " + vehicleId
            );
        }
    }

    public List<MaintenanceResponse> getMaintenanceByStatus(Maintenance.MaintenanceStatus status) {
        List<Maintenance> maintenances = maintenanceRepository.findByStatus(status);
        return maintenances.stream()
                .map(this::maintenanceToMaintenanceResponse)
                .collect(Collectors.toList());
    }

    public MaintenanceResponse getMaintenanceById(String maintenanceId) {
        try {
            ObjectId id = new ObjectId(maintenanceId);
            Maintenance maintenance = maintenanceRepository.findById(id)
                    .orElseThrow(() -> new ApiException(
                            "Maintenance not found",
                            HttpStatus.NOT_FOUND.value(),
                            "No maintenance record found with id: " + maintenanceId
                    ));
            return maintenanceToMaintenanceResponse(maintenance);
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    "Invalid object id",
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid maintenance ID format: " + maintenanceId
            );
        }
    }

    public List<MaintenanceResponse> getAllMaintenanceRecords() {
        List<Maintenance> maintenances = maintenanceRepository.findAll();
        return maintenances.stream()
                .map(this::maintenanceToMaintenanceResponse)
                .collect(Collectors.toList());
    }

    public List<VehicleResponse> findAvailableVehicles() {
        return vehicleRepository.findByState(Vehicle.VehicleState.AVAILABLE).stream()
                .map(this::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }

    public List<VehicleResponse> findRentedVehicles() {
        return vehicleRepository.findByState(Vehicle.VehicleState.RENTED).stream()
                .map(this::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }

    public List<VehicleResponse> findRepairVehicles() {
        return vehicleRepository.findByState(Vehicle.VehicleState.REPAIR).stream()
                .map(this::vehicleToVehicleResponse)
                .collect(Collectors.toList());
    }

    private MaintenanceResponse maintenanceToMaintenanceResponse(Maintenance maintenance) {
        return MaintenanceResponse.builder()
                .maintenanceId(maintenance.getMaintenanceId().toString())
                .vehicleId(maintenance.getVehicleId().toString())
                .status(maintenance.getStatus())
                .startDate(maintenance.getStartDate())
                .endDate(maintenance.getEndDate())
                .description(maintenance.getDescription())
                .cost(maintenance.getCost())
                .build();
    }

    private VehicleResponse vehicleToVehicleResponse(Vehicle vehicle) {
        if (vehicle == null) {
            return null;
        }

        return VehicleResponse.builder()
                .id(vehicle.getVehicleId().toString())
                .registrationNumber(vehicle.getRegistrationNumber())
                .vehicleType(vehicle.getVehicleType())
                .vehicleName(vehicle.getVehicleName())
                .seatingCapacity(vehicle.getSeatingCapacity())
                .fuelType(vehicle.getFuelType())
                .status(vehicle.getState())
                .perHourRate(vehicle.getPerHourRate())
                .perKilometerRate(vehicle.getPerKilometerRate())
                .sellingPrice(vehicle.getSellingPrice())
                .hasAC(vehicle.isHasAC())
                .premium(vehicle.isPremium())
                .addedOn(vehicle.getAddedOn())
                .build();
    }

    public MaintenanceResponse completeRepair(String maintenanceId, double cost, String description) {
        try {
            ObjectId id = new ObjectId(maintenanceId);
            Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new ApiException(
                    "Invalid maintenance id",
                    HttpStatus.BAD_REQUEST.value(),
                    "no maintenance record found with the id: " + maintenanceId
            ));

            Vehicle vehicle = vehicleRepository.findById(maintenance.getVehicleId()).orElseThrow(() -> new ApiException(
                    "Invalid vehicle ID",
                    HttpStatus.BAD_REQUEST.value(),
                    "Vehicle not found with id: " + maintenance.getVehicleId()
            ));

            vehicle.setState(Vehicle.VehicleState.AVAILABLE);
            vehicleRepository.save(vehicle);

            maintenance.setCost(cost);
            maintenance.setStatus(Maintenance.MaintenanceStatus.COMPLETED);
            maintenance.setDescription(description);
            maintenance.setEndDate(LocalDateTime.now());

            return maintenanceToMaintenanceResponse(maintenanceRepository.save(maintenance));
        } catch (IllegalArgumentException e) {
            throw new ApiException("invalid object id",HttpStatus.BAD_REQUEST.value(), "Invalid vehicle ID format: " + maintenanceId);
        }
    }
}
