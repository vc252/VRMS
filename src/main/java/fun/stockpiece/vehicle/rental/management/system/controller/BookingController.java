package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.*;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.security.PrincipalUser;
import fun.stockpiece.vehicle.rental.management.system.service.BookingService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
@AllArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO bookingRequest,@AuthenticationPrincipal PrincipalUser userDetails) {
        ObjectId userId = userDetails.getUser().getUserId();
        BookingResponseDTO booking = bookingService.createBooking(bookingRequest,userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<BookingResponseDTO>builder()
                        .success(true)
                        .message("Booking created successfully")
                        .data(booking)
                        .status(HttpStatus.CREATED)
                        .build()
        );
    }

    @PostMapping("/cancel/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        BookingResponseDTO booking = bookingService.cancelBooking(bookingId);
        return ResponseEntity.ok(
                ApiResponse.<BookingResponseDTO>builder()
                        .success(true)
                        .message("Booking cancelled successfully")
                        .data(booking)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/start/{bookingId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> startBooking(
            @PathVariable String bookingId,
            @RequestBody StartBookingRequestDTO startRequest,
            @AuthenticationPrincipal PrincipalUser userDetails) {
        User user = userDetails.getUser();
        DutySlipResponseDTO dutySlip = bookingService.startBooking(bookingId, startRequest.getStartOdometer(), user);
        return ResponseEntity.ok(
                ApiResponse.<DutySlipResponseDTO>builder()
                        .success(true)
                        .message("Booking started successfully")
                        .data(dutySlip)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @PostMapping("/complete/{bookingId}")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> completeBooking(
            @PathVariable String bookingId,
            @RequestBody CompleteBookingRequestDTO completeRequest,
            @AuthenticationPrincipal PrincipalUser userDetails) {
        User user = userDetails.getUser();
        InvoiceResponseDTO invoice = bookingService.completeBooking(
                bookingId,
                completeRequest.getEndOdometer(),
                completeRequest.getNightHalt(),
                completeRequest.getComments(),
                user);

        return ResponseEntity.ok(
                ApiResponse.<InvoiceResponseDTO>builder()
                        .success(true)
                        .message("Booking completed successfully")
                        .data(invoice)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<?> getCustomerBookings(@AuthenticationPrincipal PrincipalUser userDetails) {
        ObjectId customerId = userDetails.getUser().getUserId();
        List<BookingResponseDTO> bookings = bookingService.getBookingsByCustomerId(customerId);
        return ResponseEntity.ok(
                ApiResponse.<List<BookingResponseDTO>>builder()
                        .success(true)
                        .message("Customer bookings retrieved successfully")
                        .data(bookings)
                        .status(HttpStatus.OK)
                        .build()
        );
    }

    @GetMapping("/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<?> getDriverBookings(@AuthenticationPrincipal PrincipalUser userDetails) {
        ObjectId driverId = userDetails.getUser().getUserId();
        List<BookingResponseDTO> bookings = bookingService.getBookingsByDriverId(driverId);
        return ResponseEntity.ok(
                ApiResponse.<List<BookingResponseDTO>>builder()
                        .success(true)
                        .message("Driver bookings retrieved successfully")
                        .data(bookings)
                        .status(HttpStatus.OK)
                        .build()
        );
    }
}