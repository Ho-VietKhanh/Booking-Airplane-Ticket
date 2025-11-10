# Tính Năng Lịch Sử Đặt Vé - Booking History Feature

## Tổng Quan
Đã tạo thành công tính năng lịch sử đặt vé cho người dùng đã đăng nhập.

## Các Thay Đổi Đã Thực Hiện

### 1. Controller - BookingController.java
**Đường dẫn:** `src/main/java/se196411/booking_ticket/controller/BookingController.java`

**Thêm endpoint mới:**
```java
@GetMapping("/history")
public String showBookingHistory(Model model)
```

**Chức năng:**
- Kiểm tra người dùng đã đăng nhập chưa
- Lấy email từ Spring Security Authentication
- Tìm thông tin user qua UserService.findByEmail()
- Lấy tất cả booking theo user_id
- Hiển thị trang lịch sử đặt vé

**URL truy cập:** `/booking/history`

### 2. Template - booking-history.html
**Đường dẫn:** `src/main/resources/templates/booking-history.html`

**Tính năng giao diện:**
- ✅ Navbar với logo Bamboo Airways
- ✅ Hiển thị danh sách booking của user
- ✅ Thông tin mỗi booking:
  - Mã đặt vé (Booking ID)
  - Ngày đặt vé
  - Trạng thái (CONFIRMED/PENDING/CANCELLED)
  - Mã thanh toán
  - Tổng tiền
- ✅ Nút "Xem chi tiết" để xem thông tin đầy đủ
- ✅ Empty state khi chưa có booking nào
- ✅ Responsive design
- ✅ Màu sắc và theme Bamboo Airways

### 3. Navigation - dashboard.html
**Đường dẫn:** `src/main/resources/templates/dashboard.html`

**Cập nhật:**
- Sửa link "Lịch sử" từ `#history` thành `/booking/history`
- Chỉ hiển thị khi user đã đăng nhập

## Cách Sử Dụng

### 1. Đăng nhập vào hệ thống
- Truy cập: `http://localhost:8080/login`
- Đăng nhập với tài khoản của bạn

### 2. Truy cập lịch sử đặt vé
**Cách 1:** Click vào nút "Lịch sử" trên thanh navbar
**Cách 2:** Truy cập trực tiếp: `http://localhost:8080/booking/history`

### 3. Xem chi tiết booking
- Click nút "Xem chi tiết" trên mỗi booking card
- Sẽ chuyển đến trang chi tiết: `/booking/detail/{bookingId}`

## Bảo Mật

### Spring Security
- Endpoint `/booking/history` đã được bảo vệ bởi Spring Security
- Chỉ user đã đăng nhập mới truy cập được
- Nếu chưa đăng nhập sẽ redirect về `/login`

### Phân quyền dữ liệu
- Mỗi user chỉ xem được booking của chính mình
- Lọc theo `user_id` từ database

## Cấu Trúc Database

### Truy vấn sử dụng
```java
BookingRepository.findAllByUserUserId(String userId)
```

### Dữ liệu hiển thị
- `booking_id` - Mã đặt vé
- `booking_time` - Thời gian đặt
- `total_amount` - Tổng tiền
- `status` - Trạng thái
- `payment_id` - Mã thanh toán

## Giao Diện

### Theme Colors
- Primary Green: `#00613A`
- Gold: `#D4AF37`
- Light Background: `#F7F7F7`

### Trạng thái Booking
- **CONFIRMED** - Màu xanh lá (#28a745)
- **PENDING** - Màu vàng (#ffc107)
- **CANCELLED** - Màu đỏ (#dc3545)

## Tính Năng Tương Lai Có Thể Mở Rộng

1. **Bộ lọc và Tìm kiếm**
   - Lọc theo trạng thái
   - Tìm kiếm theo mã booking
   - Lọc theo khoảng thời gian

2. **Phân trang**
   - Thêm pagination khi có nhiều booking

3. **Export PDF/Excel**
   - Xuất lịch sử đặt vé ra file

4. **Thống kê**
   - Tổng số tiền đã chi
   - Số chuyến bay đã đi
   - Điểm tích lũy

5. **Hủy vé trực tiếp**
   - Thêm nút hủy vé từ trang lịch sử

## Kiểm Tra

### Test Cases
1. ✅ User chưa đăng nhập truy cập `/booking/history` → Redirect to login
2. ✅ User đã đăng nhập nhưng chưa có booking → Hiển thị empty state
3. ✅ User đã đăng nhập có booking → Hiển thị danh sách booking
4. ✅ Click "Xem chi tiết" → Chuyển đến trang chi tiết booking

## Hỗ Trợ

Nếu có vấn đề:
1. Kiểm tra user đã đăng nhập
2. Kiểm tra database có booking với user_id tương ứng
3. Xem log lỗi trong console
4. Kiểm tra Spring Security configuration

---
**Ngày tạo:** 10/11/2025
**Phiên bản:** 1.0

