package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.*;
import fun.stockpiece.vehicle.rental.management.system.model.Maintenance;
import fun.stockpiece.vehicle.rental.management.system.model.Vehicle;
import fun.stockpiece.vehicle.rental.management.system.repository.VehicleRepository;
import fun.stockpiece.vehicle.rental.management.system.service.VehicleService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
@AllArgsConstructor
public class VehicleController {
    private final VehicleService vehicleService;
    private final VehicleRepository vehicleRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> addVehicle(@RequestBody VehicleRequestDTO vehicleRequest) {
        Vehicle vehicle = Vehicle.builder()
                .registrationNumber(vehicleRequest.getRegistrationNumber())
                .vehicleType(vehicleRequest.getVehicleType())
                .vehicleName(vehicleRequest.getVehicleName())
                .seatingCapacity(vehicleRequest.getSeatingCapacity())
                .fuelType(vehicleRequest.getFuelType())
                .perHourRate(vehicleRequest.getPerHourRate())
                .perKilometerRate(vehicleRequest.getPerKilometerRate())
                .sellingPrice(vehicleRequest.getSellingPrice())
                .hasAC(vehicleRequest.isHasAC())
                .premium(vehicleRequest.isPremium())
                .build();

        VehicleResponse savedVehicle = vehicleService.addVehicle(vehicle);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<VehicleResponse>builder()
                        .success(true)
                        .message("Vehicle created successfully")
                        .data(savedVehicle)
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @DeleteMapping("/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> sellVehicle(@PathVariable String vehicleId) {
        int sellingPrice = vehicleService.sellVehicle(vehicleId);
        Map<String,Integer> response = new HashMap<>();
        response.put("sellingPrice",sellingPrice);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Map<String,Integer>>builder()
                        .success(true)
                        .message("Vehicle sold successfully")
                        .data(response)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/repair/{vehicleId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> sendVehicleForRepair(@PathVariable String vehicleId) {
        MaintenanceResponse maintenance = vehicleService.sendVehicleForRepair(vehicleId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MaintenanceResponse>builder()
                        .success(true)
                        .message("Vehicle sent for repair")
                        .data(maintenance)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PutMapping("/repair/complete/{maintenanceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> completeRepair(@PathVariable String maintenanceId,
                                                      @RequestBody CompleteRepairRequest request) {
        MaintenanceResponse maintenance = vehicleService.completeRepair(maintenanceId, request.getCost(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<MaintenanceResponse>builder()
                        .success(true)
                        .message("Vehicle repair complete")
                        .data(maintenance)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/available")
    public ResponseEntity<?> getAvailableVehicles() {
        List<VehicleResponse> vehicles = vehicleService.findAvailableVehicles();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<List<VehicleResponse>>builder()
                        .success(true)
                        .message("Vehicle sent for repair")
                        .data(vehicles)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/rented")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRentedVehicles() {
        List<VehicleResponse> vehicles = vehicleService.findRentedVehicles();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<List<VehicleResponse>>builder()
                        .success(true)
                        .message("Vehicle sent for repair")
                        .data(vehicles)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/repair")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getRepairVehicles() {
        List<VehicleResponse> vehicles = vehicleService.findRepairVehicles();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<List<VehicleResponse>>builder()
                        .success(true)
                        .message("Vehicle sent for repair")
                        .data(vehicles)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/maintenance")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getAllMaintenanceRecords() {
        List<MaintenanceResponse> maintenances = vehicleService.getAllMaintenanceRecords();
        return ResponseEntity.ok(
                ApiResponse.<List<MaintenanceResponse>>builder()
                        .success(true)
                        .message("All maintenance records retrieved successfully")
                        .data(maintenances)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/maintenance/{maintenanceId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getMaintenanceById(@PathVariable String maintenanceId) {
        MaintenanceResponse maintenance = vehicleService.getMaintenanceById(maintenanceId);
        return ResponseEntity.ok(
                ApiResponse.<MaintenanceResponse>builder()
                        .success(true)
                        .message("Maintenance record retrieved successfully")
                        .data(maintenance)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/maintenance/status/{status}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getMaintenanceByStatus(@PathVariable String status) {
        try {
            Maintenance.MaintenanceStatus maintenanceStatus = Maintenance.MaintenanceStatus.valueOf(status.toUpperCase());
            List<MaintenanceResponse> maintenances = vehicleService.getMaintenanceByStatus(maintenanceStatus);

            return ResponseEntity.ok(
                    ApiResponse.<List<MaintenanceResponse>>builder()
                            .success(true)
                            .message("Maintenance records with status " + status + " retrieved successfully")
                            .data(maintenances)
                            .status(HttpStatus.OK)
                            .build()
            );
        } catch (IllegalArgumentException e) {
            throw new ApiException(
                    "Invalid status",
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid maintenance status: " + status + ". Valid values are ONGOING and COMPLETED"
            );
        }
    }
}
