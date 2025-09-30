package swp.project.swp391.serviceImp.customer;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import swp.project.swp391.constant.ErrorHandler;
import swp.project.swp391.entity.Customer;
import swp.project.swp391.entity.User;
import swp.project.swp391.exception.BaseException;
import swp.project.swp391.repository.CustomerRepository;
import swp.project.swp391.repository.UserRepository;
import swp.project.swp391.request.customer.UpdateCustomerProfileRequest;
import swp.project.swp391.response.customer.CustomerProfileResponse;
import swp.project.swp391.security.Ownership;
import swp.project.swp391.security.RbacGuard;
import swp.project.swp391.service.customer.CustomerService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final RbacGuard guard;
    private final Ownership ownership;

    private User me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (User) auth.getPrincipal();
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfile(Long customerId, UpdateCustomerProfileRequest request) {
        User current = me();

        // enforce ANY || OWNER
        guard.requireAnyOrOwner(
                current,
                "customer.update.any",
                () -> ownership.isCustomerOwner(customerId, current.getId())
        );

        // lấy dữ liệu: nếu không có *.any thì truy vấn ràng buộc owner
        Customer customer = guard.has(current, "customer.update.any")
                ? customerRepository.findById(customerId)
                .orElseThrow(() -> new BaseException(ErrorHandler.CUSTOMER_NOT_FOUND))
                : customerRepository.findByIdAndUserId(customerId, current.getId())
                .orElseThrow(() -> new BaseException(ErrorHandler.FORBIDDEN));

        User target = customer.getUser();
        if (Boolean.FALSE.equals(target.getIsActive())) {
            throw new BaseException(ErrorHandler.ACCOUNT_BLOCKED);
        }

        // update User fields
        if (request.getFullName() != null) target.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) target.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) target.setAddress(request.getAddress());
        if (request.getDateOfBirth() != null) {
            target.setDateOfBirth(LocalDate.parse(
                    request.getDateOfBirth(),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
            ).atStartOfDay());
        }
        if (request.getGender() != null) {
            target.setGender(User.Gender.valueOf(request.getGender().toUpperCase()));
        }

        // update Customer fields
        if (request.getOccupation() != null) customer.setOccupation(request.getOccupation());
        if (request.getIncomeLevel() != null) {
            customer.setIncomeLevel(Customer.IncomeLevel.valueOf(request.getIncomeLevel().toUpperCase()));
        }

        userRepository.save(target);
        customerRepository.save(customer);

        return CustomerProfileResponse.fromEntity(customer);
    }


    @Override
    public CustomerProfileResponse getProfile(Long customerId) {
        User current = me();

        guard.requireAnyOrOwner(
                current,
                "customer.read.any",
                () -> ownership.isCustomerOwner(customerId, current.getId())
        );

        Customer customer = guard.has(current, "customer.read.any")
                ? customerRepository.findById(customerId)
                .orElseThrow(() -> new BaseException(ErrorHandler.CUSTOMER_NOT_FOUND))
                : customerRepository.findByIdAndUserId(customerId, current.getId())
                .orElseThrow(() -> new BaseException(ErrorHandler.FORBIDDEN));

        User target = customer.getUser();
        if (Boolean.FALSE.equals(target.getIsActive())) {
            throw new BaseException(ErrorHandler.ACCOUNT_BLOCKED);
        }

        return CustomerProfileResponse.fromEntity(customer);
    }

    @Override
    public CustomerProfileResponse getProfileByUserId(Long userId) {
        Customer c = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ErrorHandler.CUSTOMER_NOT_FOUND));
        if (Boolean.FALSE.equals(c.getUser().getIsActive())) {
            throw new BaseException(ErrorHandler.ACCOUNT_BLOCKED);
        }
        return CustomerProfileResponse.fromEntity(c);
    }

    @Override
    @Transactional
    public CustomerProfileResponse updateProfileByUser(Long userId, UpdateCustomerProfileRequest request) {
        Customer c = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new BaseException(ErrorHandler.CUSTOMER_NOT_FOUND));
        return updateProfile(c.getId(), request); // tái dùng logic
    }
}
