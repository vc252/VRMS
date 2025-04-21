package fun.stockpiece.vehicle.rental.management.system.repository;

import fun.stockpiece.vehicle.rental.management.system.model.ApprovalRequest;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ApprovalRequestRepository extends MongoRepository<ApprovalRequest, ObjectId> {
    List<ApprovalRequest> findBySubmittedBy(ObjectId submittedBy);
    List<ApprovalRequest> findByStatus(ApprovalRequest.ApprovalStatus status);
}
