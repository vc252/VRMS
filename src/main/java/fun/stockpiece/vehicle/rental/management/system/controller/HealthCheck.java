package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class HealthCheck {
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
