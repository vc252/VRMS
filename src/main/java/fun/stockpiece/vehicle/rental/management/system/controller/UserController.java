package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.*;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.security.PrincipalUser;
import fun.stockpiece.vehicle.rental.management.system.service.EmailService;
import fun.stockpiece.vehicle.rental.management.system.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @PostMapping("/register/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody @Valid UserRegistrationDTO customer) {
        User savedUser = userService.registerCustomer(userService.convertUserRegistrationDTOtoCustomer(customer));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<Customer>builder()
                        .success(true)
                        .message("customer created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("register/admin")
    public ResponseEntity<?> registerAdmin(@RequestBody @Valid UserRegistrationDTO admin) {
        User savedUser = userService.registerAdmin(userService.convertUserRegistrationDTOtoAdmin(admin));

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<User>builder()
                        .success(true)
                        .message("Admin created successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("register/driver")
    public ResponseEntity<?> registerDriver(@RequestBody @Valid UserRegistrationDTO driver) {
        User savedUser = userService.registerDriver(userService.convertUserRegistrationDTOtoDriver(driver));

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
                        .message("user logged in successfully")
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("/auth/otp")
    public ResponseEntity<?> sendOtp() throws IOException {
        PrincipalUser userDetails = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        HttpStatus httpStatus = emailService.sendEmailVerificationOTP(userDetails.getEmail());

        return ResponseEntity.status(httpStatus).body(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("email sent successfully")
                        .status(httpStatus)
                        .build()
        );
    }

    @PostMapping("/auth/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody @Valid OtpVerificationDTO otpVerificationDTO) {
        PrincipalUser userDetails = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean verified = emailService.verifyOtp(userDetails.getEmail(),otpVerificationDTO.getOtp());

        if (!verified) {
            throw new ApiException("invalid otp",HttpStatus.BAD_REQUEST.value(), "provide a valid otp and email");
        }

        userService.markAccountAsVerified(userDetails.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("email verified")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/auth/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody @Valid OtpVerificationDTO otpVerificationDTO) {
        PrincipalUser userDetails = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean verified = emailService.verifyOtp(userDetails.getEmail(),otpVerificationDTO.getOtp());

        if (!verified) {
            throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST.value(),
                    "The provided OTP is incorrect or expired");
        }

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.<Object>builder()
                        .success(true)
                        .message("otp verified")
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(@RequestBody @Valid ProfileUpdateDTO profileUpdateDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User updatedUser = userService.updateUserProfile(username, profileUpdateDTO);

        return ResponseEntity.ok(
                ApiResponse.<UserResponseDTO>builder()
                        .success(true)
                        .message("Profile updated successfully")
                        .data(userService.convertUserToUserResponseDTO(updatedUser))
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/auth/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody @Valid PasswordUpdateDTO passwordUpdateDTO) {
        PrincipalUser userDetails = (PrincipalUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        boolean otpVerified = emailService.verifyOtp(userDetails.getEmail(),passwordUpdateDTO.getOtp());

        if (!otpVerified) {
            throw new ApiException("Invalid OTP", HttpStatus.BAD_REQUEST.value(),
                    "The provided OTP is incorrect or expired");
        }

        userService.updatePassword(userDetails.getEmail(), passwordUpdateDTO.getNewPassword());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .success(true)
                        .message("Password updated successfully")
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}
