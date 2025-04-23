package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.Driver;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<Driver> findByRolesContainingAndIsApprovedTrueAndIsAvailableTrue(User.UserRole role);
}
