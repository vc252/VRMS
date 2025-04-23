package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.Maintenance;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MaintenanceRepository extends MongoRepository<Maintenance, ObjectId> {
    List<Maintenance> findByStatus(Maintenance.MaintenanceStatus status);
}