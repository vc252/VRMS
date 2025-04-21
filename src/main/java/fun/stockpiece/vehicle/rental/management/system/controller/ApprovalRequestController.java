package fun.stockpiece.vehicle.rental.management.system.controller;

import fun.stockpiece.vehicle.rental.management.system.dto.ApiResponse;
import fun.stockpiece.vehicle.rental.management.system.dto.PendingRequestDTO;
import fun.stockpiece.vehicle.rental.management.system.model.User;
import fun.stockpiece.vehicle.rental.management.system.security.PrincipalUser;
import fun.stockpiece.vehicle.rental.management.system.service.ApprovalRequestService;
import fun.stockpiece.vehicle.rental.management.system.service.CustomerService;
import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/approval-request")
@AllArgsConstructor
public class ApprovalRequestController {
    private final ApprovalRequestService approvalRequestService;

    @PostMapping
    @PreAuthorize("hasRole('DRIVER') or hasRole('CUSTOMER')")
    public ResponseEntity<?> requestApproval(@AuthenticationPrincipal PrincipalUser userDetails) {
        User user = userDetails.getUser();
        approvalRequestService.requestApproval(user);

        return ResponseEntity.status(HttpStatus.OK).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .message("approval request created")
                        .build()
        );
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> getPendingApprovalRequest() {
        List<PendingRequestDTO> approvalRequests = approvalRequestService.getPendingApprovalRequest();
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.<List<PendingRequestDTO>>builder()
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .message("pending request fetched successfully")
                        .data(approvalRequests)
                        .build()
        );
    }

    @PostMapping("/approve/{request_id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
    public ResponseEntity<?> approveRequest(@PathVariable String request_id, @AuthenticationPrincipal PrincipalUser adminDetails) {
        ObjectId id = new ObjectId(request_id);
        approvalRequestService.approveRequest(id,adminDetails.getUser().getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.builder()
                        .status(HttpStatus.CREATED)
                        .success(true)
                        .message("request approved")
                        .build()
        );
    }
}

