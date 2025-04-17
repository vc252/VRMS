package fun.stockpiece.vehicle.rental.management.system.service;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
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
        if (userRepository.findByUsername(admin.getUsername()).isPresent()) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(), "use a different username to register");
        }
        admin.setPassword(ArgonUtil.hashPassword(admin.getPassword()));
        if (admin.getRoles() == null || !admin.getRoles().contains(User.UserRole.ADMIN)) {
            admin.addRole(User.UserRole.ADMIN); // Fallback for deserialization
        }
        return userRepository.save(admin);
    }

    public User registerCustomer(Customer customer) {
        validateUser(customer);
        if (userRepository.findByUsername(customer.getUsername()).isPresent()) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(), "use a different username to register");
        }
        customer.setPassword(ArgonUtil.hashPassword(customer.getPassword()));
        if (customer.getRoles() == null || !customer.getRoles().contains(User.UserRole.CUSTOMER)) {
            customer.addRole(User.UserRole.CUSTOMER); // Fallback for deserialization
        }
        return userRepository.save(customer);
    }

    public User registerDriver(Driver driver) {
        validateUser(driver);
        if (userRepository.findByUsername(driver.getUsername()).isPresent()) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(), "use a different username to register");
        }
        driver.setPassword(ArgonUtil.hashPassword(driver.getPassword()));
        if (driver.getRoles() == null || !driver.getRoles().contains(User.UserRole.DRIVER)) {
            driver.addRole(User.UserRole.DRIVER); // Fallback for deserialization
        }
        return userRepository.save(driver);
    }

    public String verifyAndGenerateJwtToken(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        PrincipalUser userDetails = (PrincipalUser)authentication.getPrincipal();
        if (authentication.isAuthenticated()) {
            return jwtUtil.generateToken(userDetails.getUser());
        } else {
            throw new ApiException("jwt token not generated",HttpStatus.UNAUTHORIZED.value(),"credentials might be wrong");
        }
    }

//    public User registerDriver(Driver driver) {
//        validateUser(driver);
//        if (userRepository.findByUsername(driver.getUsername()).isPresent()) {
//            throw new IllegalArgumentException("Username already exists");
//        }
//        driver.setPassword(passwordEncoder.encode(driver.getPassword()));
//        driver.addRole(UserRole.DRIVER);
//        return userRepository.save(driver);
//    }

    public Optional<User> findByUsername(String Username) {
        return userRepository.findByUsername(Username);
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
}