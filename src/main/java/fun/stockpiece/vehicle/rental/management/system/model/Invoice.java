package fun.stockpiece.vehicle.rental.management.system.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "invoice")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Invoice {
    @Id
    private ObjectId invoiceId;
    private ObjectId bookingId;
    private double totalAmount;
    private LocalDateTime generationDate;
    private LocalDateTime dueDate;
    private InvoiceStatus status;

    public static enum InvoiceStatus {
        PENDING,
        PAID
    }
}
