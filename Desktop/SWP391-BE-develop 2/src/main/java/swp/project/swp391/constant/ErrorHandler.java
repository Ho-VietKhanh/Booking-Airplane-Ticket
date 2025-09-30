package swp.project.swp391.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorHandler {

    // Authentication Errors
    USERNAME_ALREADY_EXISTS(HttpStatus.CONFLICT, 409, "Tên đăng nhập đã tồn tại"),
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, 409, "Email đã tồn tại"),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, 401, "Tên đăng nhập hoặc mật khẩu không đúng"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, 401, "Token không hợp lệ hoặc đã hết hạn"),
    UNVERIFIED_ACCOUNT_EXISTS(HttpStatus.FORBIDDEN, 403, "Vui lòng kiểm tra email để xác minh tài khoản của bạn."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Không tìm thấy người dùng"),
    ACCOUNT_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, 400, "Tài khoản đã được xác minh"),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, 400, "Địa chỉ email không hợp lệ hoặc không tồn tại."),
    // User Errors
    ACCOUNT_NOT_VERIFIED(HttpStatus.FORBIDDEN, 403, "Tài khoản chưa được xác minh. Vui lòng kiểm tra email để xác minh tài khoản của bạn."),
    ID_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, 409, "Số CMND/CCCD đã tồn tại"),
    PHONE_NUMBER_ALREADY_EXISTS(HttpStatus.CONFLICT, 409, "Số điện thoại đã tồn tại"),
    INVALID_GENDER(HttpStatus.BAD_REQUEST, 400, "Giới tính không hợp lệ."),
    INVALID_INCOME_LEVEL(HttpStatus.BAD_REQUEST, 400, "Mức thu nhập không hợp lệ."),
    CUSTOMER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Không tìm thấy khách hàng"),
    ACCOUNT_BLOCKED(HttpStatus.FORBIDDEN, 403, "Tài khoản của bạn đã bị khoá. Vui lòng liên hệ quản trị viên để biết thêm chi tiết."),
    // Role Errors
    ROLE_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Không tìm thấy vai trò"),

    // Common/Validation Errors
    INVALID_DATE_FORMAT(HttpStatus.BAD_REQUEST, 400, "Định dạng ngày sinh không hợp lệ. Vui lòng sử dụng định dạng yyyy-MM-dd"),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, 400, "Invalid request"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, 403, "Bạn không có quyền truy cập vào tài nguyên này"),
    FORBIDDEN(HttpStatus.FORBIDDEN, 403, "Hành động bị cấm"),
    REQUEST_OTP_TOO_SOON(HttpStatus.TOO_MANY_REQUESTS, 429, "Bạn đã yêu cầu gửi lại mã OTP quá sớm. Vui lòng thử lại sau."),
    OTP_EXPIRED(HttpStatus.BAD_REQUEST, 400, "Mã OTP không hợp lệ hoặc đã hết hạn"),
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST, 400, "Mật khẩu mới và xác nhận mật khẩu không khớp"),
    // Dealer Errors
    DEALER_REQUIRED(HttpStatus.BAD_REQUEST, 400, "dealerId là bắt buộc đối với vai trò DEALER_MANAGER và DEALER_STAFF"),
    DEALER_NOT_FOUND(HttpStatus.NOT_FOUND, 404, "Không tìm thấy đại lý"),
    DEALER_NOT_ALLOWED(HttpStatus.BAD_REQUEST, 400, "Không được phép cung cấp dealerId cho vai trò EVM_STAFF");
    private final HttpStatus status;
    private final int code;
    private final String message;
}