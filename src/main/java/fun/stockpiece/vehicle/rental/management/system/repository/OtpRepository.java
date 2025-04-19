package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.Otp;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface OtpRepository extends MongoRepository<Otp, ObjectId> {
    boolean existsByEmail(String email);
    boolean existsByCode(String code);

    Optional<Otp> findByEmail(String email);
}
