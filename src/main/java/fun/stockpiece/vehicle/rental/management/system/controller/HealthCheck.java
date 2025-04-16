package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/health-check")
public class HealthCheck {
    @GetMapping
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("server running")
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
