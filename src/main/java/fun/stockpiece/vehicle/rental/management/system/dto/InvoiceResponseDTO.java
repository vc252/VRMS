package fun.stockpiece.vehicle.rental.management.system.dto;

import fun.stockpiece.vehicle.rental.management.system.model.Invoice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceResponseDTO {
    private String invoiceId;
    private String bookingId;
    private double totalAmount;
    private LocalDateTime generationDate;
    private LocalDateTime dueDate;
    private Invoice.InvoiceStatus status;
    private DutySlipResponseDTO dutySlip;
    private BookingResponseDTO booking;
}