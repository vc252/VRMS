package fun.stockpiece.vehicle.rental.management.system.service;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiException;
import fun.stockpiece.vehicle.rental.management.system.dto.PendingRequestDTO;
import fun.stockpiece.vehicle.rental.management.system.model.ApprovalRequest;
import fun.stockpiece.vehicle.rental.management.system.model.Customer;
import fun.stockpiece.vehicle.rental.management.system.model.Driver;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.repository.ApprovalRequestRepository;
import fun.stockpiece.vehicle.rental.management.system.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

//this is common to both customer and driver so we should create a separate service
@Service
@AllArgsConstructor
public class ApprovalRequestService {
    private final ApprovalRequestRepository approvalRequestRepository;
    private final UserRepository userRepository;

    public void requestApproval(User user) {
        if (!user.isProfileComplete()) {
            throw new ApiException("incomplete profile",HttpStatus.BAD_REQUEST.value(), "complete your profile before submitting for approval");
        }
        List<ApprovalRequest> approvalRequests = approvalRequestRepository.findBySubmittedBy(user.getUserId());
        if (approvalRequests.stream().anyMatch(approvalRequest -> approvalRequest.getStatus().equals(ApprovalRequest.ApprovalStatus.PENDING))) {
            throw new ApiException("existing pending request",HttpStatus.CONFLICT.value(), "user has already requested for approval");
        }
        Optional<ApprovalRequest> approvalRequestOptional = approvalRequests.stream().max(Comparator.comparing(ApprovalRequest::getSubmittedAt));
        if (approvalRequestOptional.isPresent() && approvalRequestOptional.get().getStatus().equals(ApprovalRequest.ApprovalStatus.APPROVED)) {
            throw new ApiException("already approved",HttpStatus.CONFLICT.value(), "cannot make a approval request again since the user is already approved");
        }

        ApprovalRequest request = ApprovalRequest.builder()
                .submittedBy(user.getUserId())
                .status(ApprovalRequest.ApprovalStatus.PENDING)
                .submittedAt(LocalDateTime.now())
                .build();

        approvalRequestRepository.save(request);
    }

    public List<PendingRequestDTO> getPendingApprovalRequest() {
        List<ApprovalRequest> pendingRequests = approvalRequestRepository.findByStatus(ApprovalRequest.ApprovalStatus.PENDING);
        return pendingRequests.stream().map(approvalRequest -> {
            User user = userRepository.findById(approvalRequest.getSubmittedBy()).orElseThrow(() -> new ApiException(
                    "Invalid approval request",
                    HttpStatus.BAD_REQUEST.value(),
                    "submittedBy field is missing or does not reference a valid user"
            ));

            return this.convertApprovalRequestToPendingRequest(approvalRequest,user);
        }).collect(Collectors.toList());
    }

    private PendingRequestDTO convertApprovalRequestToPendingRequest(ApprovalRequest approvalRequest, User user) {
        PendingRequestDTO.PendingRequestDTOBuilder builder = PendingRequestDTO.builder()
                .requestId(approvalRequest.getId().toString())
                .userId(user.getUserId().toString())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullname(user.getFullname())
                .roles(user.getRoles())
                .submittedAt(approvalRequest.getSubmittedAt())
                .address(user.getAddress())
                .phoneNumber(user.getPhoneNumber())
                .isEmailVerified(user.isEmailVerified());

        if (user.getRoles().contains(User.UserRole.CUSTOMER)) {
            Customer customer = (Customer) user;
            builder.depositAmount(customer.getDepositAmount());
        }

        return builder.build();
    }

    public void approveRequest(ObjectId requestId, ObjectId reviewerId) {
        ApprovalRequest request = approvalRequestRepository.findById(requestId)
                .orElseThrow(() -> new ApiException(
                        "Approval request not found",
                        HttpStatus.BAD_REQUEST.value(),
                        "provide a valid request id"
                ));

        if (request.getStatus() != ApprovalRequest.ApprovalStatus.PENDING) {
            throw new ApiException("request is not pending",HttpStatus.BAD_REQUEST.value(), "the request is either already approved or rejected");
        }

        User user = userRepository.findById(request.getSubmittedBy())
                .orElseThrow(() -> new ApiException(
                        "Invalid approval request",
                        HttpStatus.BAD_REQUEST.value(),
                        "submittedBy field is missing or does not reference a valid user"
                ));
        if (user.getRoles().contains(User.UserRole.CUSTOMER)) {
            Customer customer = (Customer) user;
            customer.setApproved(true);
            userRepository.save(customer);
        } else if (user.getRoles().contains(User.UserRole.DRIVER)) {
            Driver driver = (Driver) user;
            driver.setApproved(true);
            userRepository.save(driver);
        }

        request.setStatus(ApprovalRequest.ApprovalStatus.APPROVED);
        request.setReviewedAt(LocalDateTime.now());
        request.setReviewedBy(reviewerId);
        approvalRequestRepository.save(request);
    }
}
