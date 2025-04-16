package fun.stockpiece.vehicle.rental.management.system.service;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.repository.UserRepository;
import fun.stockpiece.vehicle.rental.management.system.util.ArgonUtil;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

//    public User registerAdmin(Admin admin) {
//        validateUser(admin);
//        if (userRepository.findByUserName(admin.getUsername()).isPresent()) {
//            throw new IllegalArgumentException("Username already exists");
//        }
//        admin.setPassword(passwordEncoder.encode(admin.getPassword()));
//        admin.addRole(UserRole.ADMIN);
//        return userRepository.save(admin);
//    }

    public User registerCustomer(Customer customer) {
        validateUser(customer);
        if (userRepository.findByUsername(customer.getUsername()).isPresent()) {
            throw new ApiException("username already exists", HttpStatus.CONFLICT.value(),"use a different username to register");
        }
        customer.setPassword(ArgonUtil.hashPassword(customer.getPassword()));
        customer.addRole(User.UserRole.CUSTOMER);
        return userRepository.save(customer);
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