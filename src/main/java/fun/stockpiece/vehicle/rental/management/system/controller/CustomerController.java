package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.security.PrincipalUser;
import fun.stockpiece.vehicle.rental.management.system.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/customer")
public class CustomerController {
    private CustomerService customerService;

    @PostMapping("/approval")
    public ResponseEntity<?> submitApproval(@AuthenticationPrincipal PrincipalUser userDetails) {

        User user = userDetails.getUser();

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Object>builder()
                        .build()
        );
    }
}
