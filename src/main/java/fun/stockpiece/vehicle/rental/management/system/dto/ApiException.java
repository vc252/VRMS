package fun.stockpiece.vehicle.rental.management.system.dto;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final int code;
    private final String details;

    public ApiException(String message, int code, String details) {
        super(message);
        this.code = code;
        this.details = details;
    }
}

