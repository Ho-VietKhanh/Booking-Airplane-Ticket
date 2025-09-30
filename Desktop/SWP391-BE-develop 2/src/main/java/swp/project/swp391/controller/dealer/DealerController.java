package swp.project.swp391.controller.dealer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import swp.project.swp391.entity.User;
import swp.project.swp391.request.dealer.DealerRequest;
import swp.project.swp391.response.dealer.DealerResponse;
import swp.project.swp391.service.dealer.DealerService;
import swp.project.swp391.security.RbacGuard;

import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/dealers")
@RequiredArgsConstructor
public class DealerController {

    private final DealerService dealerService;
    private final RbacGuard guard;

    // API để tạo Dealer
    @PostMapping("/api/create")
    public ResponseEntity<DealerResponse> createDealer(@Valid @RequestBody DealerRequest dealerRequest) {
        User currentUser = guard.me(); // Lấy người dùng hiện tại bằng `me()`

        // Gọi service để tạo Dealer
        DealerResponse dealerResponse = dealerService.createDealer(dealerRequest, currentUser);

        // Trả về phản hồi
        return new ResponseEntity<>(dealerResponse, HttpStatus.CREATED);
    }

    // API để đánh dấu dealer là inactive
    @PatchMapping("/{dealerId}/inactive")
    public ResponseEntity<DealerResponse> inactiveDealer(@PathVariable Long dealerId) {
        User currentUser = guard.me(); // Lấy người dùng hiện tại

        // Gọi service để inactive dealer
        DealerResponse dealerResponse = dealerService.inactiveDealer(dealerId, currentUser);

        // Trả về phản hồi
        return new ResponseEntity<>(dealerResponse, HttpStatus.OK);
    }

    // API để reactivate dealer (kích hoạt lại)
    @PatchMapping("/{dealerId}/reactivate")
    public ResponseEntity<DealerResponse> reactivateDealer(@PathVariable Long dealerId) {
        User currentUser = guard.me(); // Lấy người dùng hiện tại

        // Gọi service để reactivate dealer
        DealerResponse dealerResponse = dealerService.reactivateDealer(dealerId, currentUser);

        // Trả về phản hồi
        return new ResponseEntity<>(dealerResponse, HttpStatus.OK);
    }

    // API để lấy danh sách tất cả các dealer
    @GetMapping("getAll")
    public ResponseEntity<List<DealerResponse>> getAllDealers(Principal principal) {
        User currentUser = null;

        // Nếu có principal (người dùng đã đăng nhập), lấy thông tin người dùng hiện tại
        if (principal != null) {
            currentUser = guard.me();  // Guard giúp lấy thông tin người dùng hiện tại
        }

        // Gọi service để lấy tất cả các dealer
        List<DealerResponse> dealerResponses = dealerService.getAllDealers(currentUser);

        return new ResponseEntity<>(dealerResponses, HttpStatus.OK);
    }



    // API để lấy thông tin của một Dealer theo dealerId
    @GetMapping("/{dealerId}")
    public ResponseEntity<DealerResponse> getDealer(@PathVariable Long dealerId, Principal principal) {
        User currentUser = null;

        // Kiểm tra nếu người dùng đã đăng nhập
        if (principal != null) {
            currentUser = guard.me();  // Lấy thông tin người dùng hiện tại
        }

        // Gọi service để lấy dealer theo ID
        DealerResponse dealerResponse = dealerService.getDealer(dealerId, currentUser);

        return new ResponseEntity<>(dealerResponse, HttpStatus.OK);
    }
    @PutMapping("/edit{dealerId}")
    public ResponseEntity<DealerResponse> editDealer(
            @PathVariable Long dealerId,
            @Valid @RequestBody DealerRequest dealerRequest,
            Principal principal) {
        User currentUser = null;

        // Kiểm tra nếu người dùng đã đăng nhập
        if (principal != null) {
            currentUser = guard.me();  // Lấy thông tin người dùng hiện tại
        }

        // Gọi service để chỉnh sửa thông tin đại lý
        DealerResponse updatedDealer = dealerService.editDealer(dealerId, dealerRequest, currentUser);

        return new ResponseEntity<>(updatedDealer, HttpStatus.OK);
    }
}
