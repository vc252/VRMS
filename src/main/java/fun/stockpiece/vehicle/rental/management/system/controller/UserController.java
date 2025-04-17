package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import fun.stockpiece.vehicle.rental.management.system.dto.UserLoginDTO;
import fun.stockpiece.vehicle.rental.management.system.model.Admin;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import fun.stockpiece.vehicle.rental.management.system.model.Driver;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

//    @PostMapping("/admin")
//    public ResponseEntity<ApiResponse<User>> registerAdmin(@RequestBody Admin admin) {
//        try {
//            User savedUser = userService.registerAdmin(admin);
//            return ResponseEntity.ok(new ApiResponse<>(200, savedUser, "Admin registered successfully"));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(400).body(new ApiResponse<>(400, null, e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(new ApiResponse<>(500, null, "Internal server error"));
//        }
//    }

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody Customer customer) {

        User savedUser = userService.registerCustomer(customer);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Customer>builder()
                        .success(true)
                        .message("customer created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody Admin admin) {
        User savedUser = userService.registerAdmin(admin);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<User>builder()
                        .success(true)
                        .message("Admin created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("register/driver")
    public ResponseEntity<?> registerDriver(@RequestBody Driver driver) {
        User savedUser = userService.registerDriver(driver);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<User>builder()
                        .success(true)
                        .message("Driver created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("/auth/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO user) {
        String accessToken = userService.verifyAndGenerateJwtToken(user.getUsername(),user.getPassword());
        Map<String,String> token = new HashMap<>();
        token.put("accessToken",accessToken);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Map>builder()
                        .success(true)
                        .data(token)
                        .message("customer created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

//    @PostMapping("/driver")
//    public ResponseEntity<ApiResponse<User>> registerDriver(@RequestBody Driver driver) {
//        try {
//            User savedUser = userService.registerDriver(driver);
//            return ResponseEntity.ok(new ApiResponse<>(200, driver, "Driver registered successfully"));
//        } catch (IllegalArgumentException e) {
//            return ResponseEntity.status(400).body(new ApiResponse<>(400, null, e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body(new ApiResponse<>(500, null, "Internal server error"));
//        }
//    }
}
