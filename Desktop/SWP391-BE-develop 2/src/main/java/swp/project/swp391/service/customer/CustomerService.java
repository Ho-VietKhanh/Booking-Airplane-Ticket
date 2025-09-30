package swp.project.swp391.service.customer;

import swp.project.swp391.request.customer.UpdateCustomerProfileRequest;
import swp.project.swp391.response.customer.CustomerProfileResponse;

public interface CustomerService {
    CustomerProfileResponse updateProfile(Long customerId, UpdateCustomerProfileRequest request);
    CustomerProfileResponse getProfile(Long customerId);
    CustomerProfileResponse getProfileByUserId(Long userId);
    CustomerProfileResponse updateProfileByUser(Long userId, UpdateCustomerProfileRequest request);

}