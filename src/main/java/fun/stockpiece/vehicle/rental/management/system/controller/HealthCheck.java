package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import fun.stockpiece.vehicle.rental.management.system.model.Driver;
import fun.stockpiece.vehicle.rental.management.system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api")
@AllArgsConstructor
public class HealthCheck {
    private final UserService userService;
    @GetMapping("/health-check")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("server running")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/health-check/driver/{driverId}/toggle-availability")
    public ResponseEntity<?> toggleDriverAvailability(@PathVariable String driverId) {
        Driver driver = userService.toggleDriverAvailability(driverId);
        boolean isAvailable = driver.isAvailable();

        return ResponseEntity.ok(
                ApiResponse.<Driver>builder()
                        .success(true)
                        .message(isAvailable ? "Driver is now available" : "Driver is now unavailable")
                        .data(driver)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/jwt-check")
    public ResponseEntity<?> jwtLoginCheck() {
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("jwt verification running")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/customer/role-check")
    public ResponseEntity<?> checkCustomerRole() {
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("jwt customer role verification running")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/admin/role-check")
    public ResponseEntity<?> checkAdminRole() {
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("jwt admin role verification running")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/driver/role-check")
    public ResponseEntity<?> checkDriverRole() {
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("jwt driver role verification running")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

}
