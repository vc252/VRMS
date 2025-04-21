package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "approval_requests")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApprovalRequest {
    @Id
    private ObjectId id;
    private ObjectId submittedBy;
    private ApprovalStatus status;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
    private ObjectId reviewedBy;
    private String comments;

    public static enum ApprovalStatus {
        PENDING,
        APPROVED,
        REJECTED
    }
}