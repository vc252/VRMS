package fun.stockpiece.vehicle.rental.management.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteRepairRequest {
    private String description;
    private double cost;
}
