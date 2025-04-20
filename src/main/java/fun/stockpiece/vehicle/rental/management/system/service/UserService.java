package fun.stockpiece.vehicle.rental.management.system.service;

import fun.stockpiece.vehicle.rental.management.system.dto.*;
import fun.stockpiece.vehicle.rental.management.system.model.Admin;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import fun.stockpiece.vehicle.rental.management.system.model.Driver;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.repository.UserRepository;
import fun.stockpiece.vehicle.rental.management.system.security.PrincipalUser;
import fun.stockpiece.vehicle.rental.management.system.util.ArgonUtil;
import fun.stockpiece.vehicle.rental.management.system.util.JwtUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public User registerAdmin(Admin admin) {
        validateUser(admin);
        if (userRepository.existsByUsername(admin.getUsername())) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(), "use a different username to register");
        }
        if (userRepository.existsByEmail(admin.getEmail())) {
            throw new ApiException("email already in use", HttpStatus.CONFLICT.value(), "use a different email to register");
        }
        admin.setPassword(ArgonUtil.hashPassword(admin.getPassword()));
        if (admin.getRoles() == null || !admin.getRoles().contains(User.UserRole.ADMIN)) {
            admin.addRole(User.UserRole.ADMIN); // Fallback for deserialization
        }
        return userRepository.save(admin);
    }

    public User registerCustomer(Customer customer) {
        validateUser(customer);
        if (userRepository.existsByUsername(customer.getUsername())) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(), "use a different username to register");
        }
        if (userRepository.existsByEmail(customer.getEmail())) {
            throw new ApiException("email already in use", HttpStatus.CONFLICT.value(), "use a different email to register");
        }
        customer.setPassword(ArgonUtil.hashPassword(customer.getPassword()));
        if (customer.getRoles() == null || !customer.getRoles().contains(User.UserRole.CUSTOMER)) {
            customer.addRole(User.UserRole.CUSTOMER); // Fallback for deserialization
        }
        return userRepository.save(customer);
    }

    public User registerDriver(Driver driver) {
        validateUser(driver);
        if (userRepository.existsByUsername(driver.getUsername())) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(), "use a different username to register");
        }
        if (userRepository.existsByEmail(driver.getEmail())) {
            throw new ApiException("email already in use", HttpStatus.CONFLICT.value(), "use a different email to register");
        }
        driver.setPassword(ArgonUtil.hashPassword(driver.getPassword()));
        if (driver.getRoles() == null || !driver.getRoles().contains(User.UserRole.DRIVER)) {
            driver.addRole(User.UserRole.DRIVER); // Fallback for deserialization
        }
        return userRepository.save(driver);
    }

    public String verifyAndGenerateJwtToken(String username, String password) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (AuthenticationException e) {
            throw new ApiException("invalid credentials",HttpStatus.BAD_REQUEST.value(), "username or password incorrect");
        }
        PrincipalUser userDetails = (PrincipalUser)authentication.getPrincipal();
        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(userDetails.getUser());
        } else {
            throw new ApiException("jwt token not generated",HttpStatus.UNAUTHORIZED.value(),"credentials might be wrong");
        }
    }

    public void markAccountAsVerified(String email) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isEmpty()) {
            throw new ApiException("invalid email",HttpStatus.BAD_REQUEST.value(), "no account exists with this email please register");
        }
        User user = existingUser.get();
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public Optional<User> findByUsername(String Username) {
        return userRepository.findByUsername(Username);
    }

    public Customer convertUserRegistrationDTOtoCustomer(UserRegistrationDTO user) {
        return Customer.builder()
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public Driver convertUserRegistrationDTOtoDriver(UserRegistrationDTO user) {
        return Driver.builder()
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    public Admin convertUserRegistrationDTOtoAdmin(UserRegistrationDTO user) {
        return Admin.builder()
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    private void validateUser(User user) {
        if (user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
    }

    public User updateUserProfile(String username, ProfileUpdateDTO profileUpdateDTO) {
        User user = findUserByUsername(username)
                .orElseThrow(() -> new ApiException("User not found", HttpStatus.NOT_FOUND.value(),
                        "No user exists with username: " + username));

        if (profileUpdateDTO.getFullname() != null && !profileUpdateDTO.getFullname().isEmpty()) {
            user.setFullname(profileUpdateDTO.getFullname());
        }

        if (profileUpdateDTO.getPhoneNumber() != null && !profileUpdateDTO.getPhoneNumber().isEmpty()) {
            user.setPhoneNumber(profileUpdateDTO.getPhoneNumber());
        }

        if (profileUpdateDTO.getAddress() != null && !profileUpdateDTO.getAddress().isEmpty()) {
            user.setAddress(profileUpdateDTO.getAddress());
        }

        return userRepository.save(user);
    }

    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void updatePassword(String email, String newPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ApiException("User not found",
                        HttpStatus.NOT_FOUND.value(),
                        "No user exists with email: " + email));

        user.setPassword(ArgonUtil.hashPassword(newPassword));

        userRepository.save(user);
    }

    public UserProfileDTO convertUserToUserProfileDTO(User user) {
        UserProfileDTO profileDTO = UserProfileDTO.builder()
                .id(user.getUserId().toString())
                .username(user.getUsername())
                .fullname(user.getFullname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .roles(user.getRoles())
                .emailVerified(user.isEmailVerified())
                .build();

        // Set role-specific fields based on user role
        if (user.getRoles().contains(User.UserRole.CUSTOMER)) {
            Customer customer = (Customer) user;
            profileDTO.setDepositAmount(customer.getDepositAmount());
            profileDTO.setIsRegular(customer.isRegular());
        } else if (user.getRoles().contains(User.UserRole.DRIVER)) {
            Driver driver = (Driver) user;
            profileDTO.setIsApproved(driver.isApproved());
            profileDTO.setIsAvailable(driver.isAvailable());
        }

        return profileDTO;
    }
}