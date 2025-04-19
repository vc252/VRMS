package fun.stockpiece.vehicle.rental.management.system.config;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
import fun.stockpiece.vehicle.rental.management.system.model.Admin;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SuperAdminInitializer implements ApplicationRunner {

    private UserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String superAdminUsername = System.getProperty("SUPER_ADMIN_USERNAME");
        String superAdminPassword = System.getProperty("SUPER_ADMIN_PASSWORD");

        if (superAdminUsername == null || superAdminPassword == null || superAdminUsername.isEmpty() || superAdminPassword.isEmpty()) {
            throw new ApiException("not able to load super admin username and password", HttpStatus.INTERNAL_SERVER_ERROR.value(), "there might be some error in loading dotenv values to system properties");
        }

        if (userRepository.findByUsername(superAdminUsername).isPresent()) {
            System.out.println("super admin exists");
            return;
        }

        Admin superAdmin = Admin.builder()
                .username(superAdminUsername)
                .password(superAdminPassword)
                .isSuperAdmin(true)
                .build();

        superAdmin.addRole(User.UserRole.SUPER_ADMIN);

        userRepository.save(superAdmin);

        System.out.println("super admin created successfully");
    }
}
