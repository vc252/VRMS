package fun.stockpiece.vehicle.rental.management.system.service;

import fun.stockpiece.vehicle.rental.management.system.dto.*;
import fun.stockpiece.vehicle.rental.management.system.model.*;
import fun.stockpiece.vehicle.rental.management.system.repository.*;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class BookingService {
    private final BookingRepository bookingRepository;
    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final DutySlipRepository dutySlipRepository;
    private final InvoiceRepository invoiceRepository;

    public BookingResponseDTO createBooking(BookingRequestDTO request, ObjectId userId) {
        // Validate minimum booking time (4 hours)
        validateBookingTime(request);

        // Check if vehicle exists and is available
        Vehicle vehicle = getAndValidateVehicle(request.getVehicleId());

        // Find an approved and available driver
        Driver driver = findAvailableDriver();

        Booking booking = Booking.builder()
                .customerId(userId)
                .vehicleId(new ObjectId(request.getVehicleId()))
                .driverId(driver.getUserId())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .destination(request.getDestination())
                .status(Booking.BookingStatus.BOOKED)
                .build();

        vehicle.setState(Vehicle.VehicleState.RENTED);
        driver.setAvailable(false);

        vehicleRepository.save(vehicle);
        userRepository.save(driver);
        Booking savedBooking = bookingRepository.save(booking);

        User driverUser = userRepository.findById(driver.getUserId())
                .orElseThrow(() -> new ApiException(
                        "Driver user not found",
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Driver user data is missing"
                ));

        return BookingResponseDTO.builder()
                .bookingId(savedBooking.getBookingId().toString())
                .customerId(savedBooking.getCustomerId().toString())
                .startDate(savedBooking.getStartDate())
                .endDate(savedBooking.getEndDate())
                .destination(savedBooking.getDestination())
                .status(savedBooking.getStatus())
                .driverId(driver.getUserId().toString())
                .driverName(driverUser.getFullname())
                .driverPhone(driverUser.getPhoneNumber())
                .vehicleId(vehicle.getVehicleId().toString())
                .vehicleName(vehicle.getVehicleName())
                .registrationNumber(vehicle.getRegistrationNumber())
                .build();
    }

    public BookingResponseDTO cancelBooking(String bookingId) {
        try {
            ObjectId id = new ObjectId(bookingId);
            Booking booking = bookingRepository.findById(id)
                    .orElseThrow(() -> new ApiException(
                            "Booking not found",
                            HttpStatus.NOT_FOUND.value(),
                            "No booking found with id: " + bookingId
                    ));

            if (booking.getStatus() == Booking.BookingStatus.COMPLETED ||
                    booking.getStatus() == Booking.BookingStatus.CANCELLED ||
                    booking.getStatus() == Booking.BookingStatus.ONGOING) {
                throw new ApiException(
                        "Cannot cancel booking",
                        HttpStatus.BAD_REQUEST.value(),
                        "Booking is already " + booking.getStatus()
                );
            }

            booking.setStatus(Booking.BookingStatus.CANCELLED);

            Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId()).orElse(null);
            if (vehicle != null) {
                vehicle.setState(Vehicle.VehicleState.AVAILABLE);
                vehicleRepository.save(vehicle);
            }

            Driver driver = (Driver) userRepository.findById(booking.getDriverId()).orElse(null);
            if (driver != null) {
                driver.setAvailable(true);
                userRepository.save(driver);
            }

            Booking savedBooking = bookingRepository.save(booking);

            User driverUser = userRepository.findById(booking.getDriverId()).orElse(null);

            return BookingResponseDTO.builder()
                    .bookingId(savedBooking.getBookingId().toString())
                    .customerId(savedBooking.getCustomerId().toString())
                    .startDate(savedBooking.getStartDate())
                    .endDate(savedBooking.getEndDate())
                    .destination(savedBooking.getDestination())
                    .status(savedBooking.getStatus())
                    .driverId(booking.getDriverId().toString())
                    .driverName(driverUser != null ? driverUser.getFullname() : null)
                    .driverPhone(driverUser != null ? driverUser.getPhoneNumber() : null)
                    .vehicleId(vehicle != null ? vehicle.getVehicleId().toString() : null)
                    .vehicleName(vehicle != null ? vehicle.getVehicleName() : null)
                    .registrationNumber(vehicle != null ? vehicle.getRegistrationNumber() : null)
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new ApiException(
                    "Invalid object id",
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid maintenance ID format: " + bookingId
            );
        }
    }

    private void validateBookingTime(BookingRequestDTO request) {
        if (request.getStartDate() == null || request.getEndDate() == null) {
            throw new ApiException(
                    "Invalid booking dates",
                    HttpStatus.BAD_REQUEST.value(),
                    "Start and end dates are required"
            );
        }

        if (request.getStartDate().isAfter(request.getEndDate())) {
            throw new ApiException(
                    "Invalid date range",
                    HttpStatus.BAD_REQUEST.value(),
                    "Start date must be before end date"
            );
        }

        if (request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new ApiException(
                    "Invalid start date",
                    HttpStatus.BAD_REQUEST.value(),
                    "Start date cannot be in the past"
            );
        }

        // Check minimum booking time (4 hours)
        Duration duration = Duration.between(request.getStartDate(), request.getEndDate());
        if (duration.toHours() < 4) {
            throw new ApiException(
                    "Invalid booking duration",
                    HttpStatus.BAD_REQUEST.value(),
                    "Minimum booking duration is 4 hours"
            );
        }
    }

    private Vehicle getAndValidateVehicle(String vehicleId) {
        ObjectId id = new ObjectId(vehicleId);
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Vehicle not found",
                        HttpStatus.NOT_FOUND.value(),
                        "No vehicle found with id: " + vehicleId
                ));

        if (vehicle.getState() != Vehicle.VehicleState.AVAILABLE) {
            throw new ApiException(
                    "Vehicle not available",
                    HttpStatus.BAD_REQUEST.value(),
                    "Vehicle is not available for booking"
            );
        }

        return vehicle;
    }

    private Driver findAvailableDriver() {
        // Query for users with DRIVER role who are approved and available
        List<Driver> availableDrivers = userRepository.findByRolesContainingAndIsApprovedTrueAndIsAvailableTrue(User.UserRole.DRIVER);

        if (availableDrivers.isEmpty()) {
            throw new ApiException(
                    "No available drivers",
                    HttpStatus.SERVICE_UNAVAILABLE.value(),
                    "No approved drivers are currently available"
            );
        }

        return availableDrivers.getFirst();
    }

    public DutySlipResponseDTO startBooking(String bookingId, int startOdometer, User currentUser) {
        ObjectId id = new ObjectId(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Booking not found",
                        HttpStatus.NOT_FOUND.value(),
                        "No booking found with id: " + bookingId
                ));

        // Verify booking status
        if (booking.getStatus() != Booking.BookingStatus.BOOKED) {
            throw new ApiException(
                    "Invalid booking status",
                    HttpStatus.BAD_REQUEST.value(),
                    "Booking must be in BOOKED status to start, current status: " + booking.getStatus()
            );
        }

        // Verify driver is assigned to this booking
        if (!booking.getDriverId().equals(currentUser.getUserId())) {
            throw new ApiException(
                    "Unauthorized",
                    HttpStatus.FORBIDDEN.value(),
                    "You are not assigned to this booking"
            );
        }

        // Update booking status
        booking.setStatus(Booking.BookingStatus.ONGOING);
        bookingRepository.save(booking);

        // Create duty slip
        DutySlip dutySlip = DutySlip.builder()
                .bookingId(booking.getBookingId())
                .startTime(LocalDateTime.now())
                .startOdometer(startOdometer)
                .build();

        DutySlip savedDutySlip = dutySlipRepository.save(dutySlip);

        return convertToDutySlipResponseDTO(savedDutySlip);
    }

    public InvoiceResponseDTO completeBooking(String bookingId, int endOdometer, int nightHalt, String comments, User currentUser) {
        ObjectId id = new ObjectId(bookingId);
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ApiException(
                        "Booking not found",
                        HttpStatus.NOT_FOUND.value(),
                        "No booking found with id: " + bookingId
                ));

        // Verify booking status
        if (booking.getStatus() != Booking.BookingStatus.ONGOING) {
            throw new ApiException(
                    "Invalid booking status",
                    HttpStatus.BAD_REQUEST.value(),
                    "Booking must be in ONGOING status to complete, current status: " + booking.getStatus()
            );
        }

        if (!booking.getDriverId().equals(currentUser.getUserId())) {
            throw new ApiException(
                    "Unauthorized",
                    HttpStatus.FORBIDDEN.value(),
                    "You are not assigned to this booking"
            );
        }

        DutySlip dutySlip = dutySlipRepository.findByBookingId(booking.getBookingId())
                .orElseThrow(() -> new ApiException(
                        "Duty slip not found",
                        HttpStatus.NOT_FOUND.value(),
                        "No duty slip found for booking: " + bookingId
                ));

        dutySlip.setEndTime(LocalDateTime.now());
        dutySlip.setEndOdometer(endOdometer);
        dutySlip.setNightHalt(nightHalt);
        dutySlip.setComments(comments);

        // Validate odometer readings
        if (endOdometer < dutySlip.getStartOdometer()) {
            throw new ApiException(
                    "Invalid odometer reading",
                    HttpStatus.BAD_REQUEST.value(),
                    "End odometer reading cannot be less than start odometer reading"
            );
        }

        Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                .orElseThrow(() -> new ApiException(
                        "Vehicle not found",
                        HttpStatus.NOT_FOUND.value(),
                        "Vehicle not found for booking: " + bookingId
                ));

        double totalAmount = calculateTotalAmount(booking, dutySlip, vehicle);

        // Update booking status
        booking.setStatus(Booking.BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        // Update vehicle and driver status
        vehicle.setState(Vehicle.VehicleState.AVAILABLE);
        vehicleRepository.save(vehicle);

        Driver driver = (Driver) userRepository.findById(booking.getDriverId()).orElse(null);
        if (driver != null) {
            driver.setAvailable(true);
            userRepository.save(driver);
        }

        DutySlip savedDutySlip = dutySlipRepository.save(dutySlip);

        LocalDateTime now = LocalDateTime.now();

        Invoice invoice = Invoice.builder()
                .bookingId(booking.getBookingId())
                .totalAmount(totalAmount)
                .generationDate(now)
                .dueDate(now.plusDays(7))
                .status(Invoice.InvoiceStatus.PENDING)
                .build();

        Invoice savedInvoice = invoiceRepository.save(invoice);

        // Create response
        InvoiceResponseDTO response = new InvoiceResponseDTO();
        response.setInvoiceId(savedInvoice.getInvoiceId().toString());
        response.setBookingId(savedInvoice.getBookingId().toString());
        response.setTotalAmount(savedInvoice.getTotalAmount());
        response.setGenerationDate(savedInvoice.getGenerationDate());
        response.setDueDate(savedInvoice.getDueDate());
        response.setStatus(savedInvoice.getStatus());
        response.setDutySlip(convertToDutySlipResponseDTO(savedDutySlip));

        // Get booking details for response
        BookingResponseDTO bookingResponseDTO = createBookingResponseDTO(booking, vehicle, driver);
        response.setBooking(bookingResponseDTO);

        return response;
    }

    private double calculateTotalAmount(Booking booking, DutySlip dutySlip, Vehicle vehicle) {
        // Calculate time-based charge
        long hours = Duration.between(dutySlip.getStartTime(), dutySlip.getEndTime()).toHours();
        if (hours < 4) hours = 4; // Minimum 4 hours
        double hourlyCharge = hours * vehicle.getPerHourRate();

        // Calculate distance-based charge
        int distance = dutySlip.getEndOdometer() - dutySlip.getStartOdometer();
        double distanceCharge = distance * vehicle.getPerKilometerRate();

        // Calculate night halt charges
        double nightHaltCharge = dutySlip.getNightHalt() * 500.0;

        // Base amount
        double baseAmount = hourlyCharge + distanceCharge + nightHaltCharge;

        // Apply AC charge if applicable
        if (vehicle.isHasAC()) {
            baseAmount *= 1.25; // 25% additional for AC
        }

        // Apply premium charge if applicable
        if (vehicle.isPremium()) {
            baseAmount *= 2; // Double for premium
        }

        return baseAmount;
    }

    private DutySlipResponseDTO convertToDutySlipResponseDTO(DutySlip dutySlip) {
        return DutySlipResponseDTO.builder()
                .dutySlipId(dutySlip.getDutySlipId().toString())
                .bookingId(dutySlip.getBookingId().toString())
                .startTime(dutySlip.getStartTime())
                .endTime(dutySlip.getEndTime())
                .startOdometer(dutySlip.getStartOdometer())
                .endOdometer(dutySlip.getEndOdometer())
                .nightHalt(dutySlip.getNightHalt())
                .comments(dutySlip.getComments())
                .build();
    }

    private BookingResponseDTO createBookingResponseDTO(Booking booking, Vehicle vehicle, User driver) {
        return BookingResponseDTO.builder()
                .bookingId(booking.getBookingId().toString())
                .customerId(booking.getCustomerId().toString())
                .startDate(booking.getStartDate())
                .endDate(booking.getEndDate())
                .destination(booking.getDestination())
                .status(booking.getStatus())
                .driverId(driver != null ? driver.getUserId().toString() : null)
                .driverName(driver != null ? driver.getFullname() : null)
                .driverPhone(driver != null ? driver.getPhoneNumber() : null)
                .vehicleId(vehicle.getVehicleId().toString())
                .vehicleName(vehicle.getVehicleName())
                .registrationNumber(vehicle.getRegistrationNumber())
                .build();
    }

    // Add these methods to your BookingService class

    public List<BookingResponseDTO> getBookingsByCustomerId(ObjectId customerId) {
        List<Booking> bookings = bookingRepository.findByCustomerId(customerId);
        return bookings.stream()
                .map(booking -> {
                    Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                            .orElse(null);

                    User driver = null;
                    if (booking.getDriverId() != null) {
                        driver = userRepository.findById(booking.getDriverId()).orElse(null);
                    }

                    return createBookingResponseDTO(booking, vehicle, driver);
                })
                .toList();
    }

    public List<BookingResponseDTO> getBookingsByDriverId(ObjectId driverId) {
        List<Booking> bookings = bookingRepository.findByDriverId(driverId);
        return bookings.stream()
                .map(booking -> {
                    Vehicle vehicle = vehicleRepository.findById(booking.getVehicleId())
                            .orElse(null);

                    User driver = userRepository.findById(booking.getDriverId()).orElse(null);
                    User customer = userRepository.findById(booking.getCustomerId()).orElse(null);

                    BookingResponseDTO dto = createBookingResponseDTO(booking, vehicle, driver);

                    return dto;
                })
                .toList();
    }

}