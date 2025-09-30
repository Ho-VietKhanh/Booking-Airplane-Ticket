package swp.project.swp391.controller.customer;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import swp.project.swp391.entity.User;
import swp.project.swp391.request.customer.UpdateCustomerProfileRequest;
import swp.project.swp391.response.customer.CustomerProfileResponse;
import swp.project.swp391.service.customer.CustomerService;

@RestController
@RequestMapping(value = "/api/customers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    // ADMIN/OWNER: update theo id
    @PutMapping(value = "/update-profile-id", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerProfileResponse> updateById(
            @PathVariable Long id,
            @Valid @RequestBody UpdateCustomerProfileRequest request
    ) {
        return ResponseEntity.ok(customerService.updateProfile(id, request));
    }

    // ADMIN/OWNER: get theo id
    @GetMapping("/view-profile/{id}")
    public ResponseEntity<CustomerProfileResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getProfile(id));
    }

    // USER/ADMIN: get chính mình
    @GetMapping("/view-profile-me")
    public ResponseEntity<CustomerProfileResponse> getMe(@AuthenticationPrincipal User me) {
        return ResponseEntity.ok(customerService.getProfileByUserId(me.getId()));
    }

    // USER/ADMIN: update chính mình
    @PutMapping(value = "/update-profile-me", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerProfileResponse> updateMe(
            @AuthenticationPrincipal User me,
            @Valid @RequestBody UpdateCustomerProfileRequest request
    ) {
        return ResponseEntity.ok(customerService.updateProfileByUser(me.getId(), request));
    }
}
