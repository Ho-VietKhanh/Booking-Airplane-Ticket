package swp.project.swp391.serviceImp.dealer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import swp.project.swp391.constant.ErrorHandler;
import swp.project.swp391.entity.Dealer;
import swp.project.swp391.entity.User;
import swp.project.swp391.exception.BaseException;
import swp.project.swp391.repository.DealerRepository;
import swp.project.swp391.repository.UserRepository;
import swp.project.swp391.request.dealer.DealerRequest;
import swp.project.swp391.response.dealer.DealerResponse;
import swp.project.swp391.security.RbacGuard;
import swp.project.swp391.service.dealer.DealerService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealerServiceImpl implements DealerService {

    private final DealerRepository dealerRepository;
    private final RbacGuard guard;

    @Override
    public DealerResponse createDealer(DealerRequest dealerRequest, User currentUser) {
        // Kiểm tra quyền của admin
        guard.require(guard.has(currentUser, "dealer.create")); // Admin có quyền này

        // Kiểm tra nếu dealer đã tồn tại với email hoặc số điện thoại
        if (dealerRepository.existsByEmail(dealerRequest.getEmail())) {
            throw new BaseException(ErrorHandler.EMAIL_ALREADY_EXISTS);
        }

        if (dealerRepository.existsByPhoneNumber(dealerRequest.getPhoneNumber())) {
            throw new BaseException(ErrorHandler.PHONE_NUMBER_ALREADY_EXISTS);
        }

        // Tạo Dealer
        Dealer dealer = new Dealer();
        dealer.setName(dealerRequest.getName());
        dealer.setAddress(dealerRequest.getAddress());
        dealer.setPhoneNumber(dealerRequest.getPhoneNumber());
        dealer.setEmail(dealerRequest.getEmail());
        dealer.setRegion(Dealer.Region.valueOf(dealerRequest.getRegion().toUpperCase())); // Convert string to enum// Gán user (admin) làm chủ sở hữu
        dealer.setIsActive(true);
        dealer.setCreatedAt(LocalDateTime.now());
        dealer.setUpdatedAt(LocalDateTime.now());

        // Lưu dealer vào DB
        dealerRepository.save(dealer);

        // Trả về DealerResponse
        return new DealerResponse(dealer.getId(), dealer.getName(), dealer.getAddress(), dealer.getPhoneNumber(), dealer.getEmail(), dealer.getIsActive(), dealer.getRegion().name());
    }
    @Override
    public DealerResponse inactiveDealer(Long dealerId, User currentUser) {
        // Kiểm tra quyền của admin
        guard.require(guard.has(currentUser, "dealer.inactive"));

        Dealer dealer = dealerRepository.findById(dealerId).orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));
        dealer.setIsActive(false);
        dealer.setUpdatedAt(LocalDateTime.now());

        // Lưu trạng thái inactive vào DB
        dealerRepository.save(dealer);

        return new DealerResponse(dealer.getId(), dealer.getName(), dealer.getAddress(), dealer.getPhoneNumber(), dealer.getEmail(), dealer.getIsActive(), dealer.getRegion().name());
    }
    @Override
    public DealerResponse reactivateDealer(Long dealerId, User currentUser) {
        // Kiểm tra quyền của admin
        guard.require(guard.has(currentUser, "dealer.reactivate"));

        Dealer dealer = dealerRepository.findById(dealerId).orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));
        dealer.setIsActive(true);
        dealer.setUpdatedAt(LocalDateTime.now());

        // Lưu trạng thái reactivate vào DB
        dealerRepository.save(dealer);

        return new DealerResponse(dealer.getId(), dealer.getName(), dealer.getAddress(), dealer.getPhoneNumber(), dealer.getEmail(), dealer.getIsActive(), dealer.getRegion().name());
    }

    @Override
    public List<DealerResponse> getAllDealers(User currentUser) {
        List<Dealer> dealers;

        // Nếu người dùng không đăng nhập (currentUser == null)
        if (currentUser == null) {
            // Trả về danh sách chỉ các dealer đang hoạt động (isActive = true)
            dealers = dealerRepository.findByIsActive(true);
        } else {
            // Kiểm tra xem người dùng có phải là admin không
            if (guard.has(currentUser, "admin")) {
                // Admin có thể xem tất cả dealer, bao gồm cả inactive dealer
                dealers = dealerRepository.findAll();
            } else {
                // Người dùng bình thường chỉ xem các dealer đang hoạt động (isActive = true)
                dealers = dealerRepository.findByIsActive(true);
            }
        }

        // Chuyển đổi thành DealerResponse và trả về
        return dealers.stream()
                .map(dealer -> new DealerResponse(
                        dealer.getId(),
                        dealer.getName(),
                        dealer.getAddress(),
                        dealer.getPhoneNumber(),
                        dealer.getEmail(),
                        dealer.getIsActive(),
                        dealer.getRegion().name()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public DealerResponse getDealer(Long dealerId, User currentUser) {
        Dealer dealer;

        // Nếu người dùng không đăng nhập (currentUser == null), chỉ hiển thị dealer đang hoạt động
        if (currentUser == null) {
            // Nếu không đăng nhập, chỉ cho phép lấy dealer đang hoạt động
            dealer = dealerRepository.findByIdAndIsActive(dealerId, true)
                    .orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));
        } else {
            // Kiểm tra nếu người dùng là admin
            if (guard.has(currentUser, "admin")) {
                // Admin có thể xem tất cả dealer, bao gồm cả inactive dealer
                dealer = dealerRepository.findById(dealerId)
                        .orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));
            } else {
                // Người dùng bình thường chỉ có thể xem dealer đang hoạt động (isActive = true)
                dealer = dealerRepository.findByIdAndIsActive(dealerId, true)
                        .orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));
            }
        }

        // Chuyển đổi thành DealerResponse và trả về
        return new DealerResponse(
                dealer.getId(),
                dealer.getName(),
                dealer.getAddress(),
                dealer.getPhoneNumber(),
                dealer.getEmail(),
                dealer.getIsActive(),
                dealer.getRegion().name()
        );
    }
    @Override
    public DealerResponse editDealer(Long dealerId, DealerRequest dealerRequest, User currentUser) {
        // Kiểm tra quyền của admin
        if (!guard.has(currentUser, "admin")) {
            throw new BaseException(ErrorHandler.FORBIDDEN);  // Chỉ admin mới có quyền sửa
        }

        // Tìm dealer theo ID
        Dealer dealer = dealerRepository.findById(dealerId)
                .orElseThrow(() -> new BaseException(ErrorHandler.DEALER_NOT_FOUND));

        // Cập nhật thông tin dealer từ DealerRequest
        dealer.setName(dealerRequest.getName());
        dealer.setAddress(dealerRequest.getAddress());
        dealer.setPhoneNumber(dealerRequest.getPhoneNumber());
        dealer.setEmail(dealerRequest.getEmail());
        dealer.setRegion(Dealer.Region.valueOf(dealerRequest.getRegion().toUpperCase())); // Cập nhật vùng
        dealer.setUpdatedAt(LocalDateTime.now());  // Cập nhật thời gian chỉnh sửa

        // Lưu lại dealer đã chỉnh sửa
        dealerRepository.save(dealer);

        // Trả về DealerResponse sau khi cập nhật
        return new DealerResponse(
                dealer.getId(),
                dealer.getName(),
                dealer.getAddress(),
                dealer.getPhoneNumber(),
                dealer.getEmail(),
                dealer.getIsActive(),
                dealer.getRegion().name()
        );
    }

}
