package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customer")
public class CustomerController {
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Customer customer) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Customer>builder()
                        .success(true)
                        .message("customer created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }
}
