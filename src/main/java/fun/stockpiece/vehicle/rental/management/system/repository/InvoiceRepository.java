package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.Invoice;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvoiceRepository extends MongoRepository<Invoice, ObjectId> {
}