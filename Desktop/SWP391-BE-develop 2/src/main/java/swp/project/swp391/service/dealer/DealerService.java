package swp.project.swp391.service.dealer;

import swp.project.swp391.request.dealer.DealerRequest;
import swp.project.swp391.response.dealer.DealerResponse;
import swp.project.swp391.entity.User;

import java.util.List;

public interface DealerService {

    // Đảm bảo phương thức này có cả DealerRequest và User
    DealerResponse createDealer(DealerRequest dealerRequest, User currentUser);

    // Phương thức inactive Dealer
    DealerResponse inactiveDealer(Long dealerId, User currentUser);

    // Phương thức reactivate Dealer
    DealerResponse reactivateDealer(Long dealerId, User currentUser);

    // Phương thức lấy danh sách tất cả Dealer
    List<DealerResponse> getAllDealers(User currentUser);

    // Phương thức mới để lấy thông tin một Dealer cụ thể
    DealerResponse getDealer(Long dealerId, User currentUser);

    DealerResponse editDealer(Long dealerId, DealerRequest dealerRequest, User currentUser);
}
