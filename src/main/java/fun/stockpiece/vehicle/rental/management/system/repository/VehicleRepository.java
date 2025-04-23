package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.Vehicle;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface VehicleRepository extends MongoRepository<Vehicle, ObjectId> {
    List<Vehicle> findByState(Vehicle.VehicleState state);
}
