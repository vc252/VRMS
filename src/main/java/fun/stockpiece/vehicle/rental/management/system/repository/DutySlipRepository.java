package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.DutySlip;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DutySlipRepository extends MongoRepository<DutySlip, ObjectId> {
    Optional<DutySlip> findByBookingId(ObjectId bookingId);
}