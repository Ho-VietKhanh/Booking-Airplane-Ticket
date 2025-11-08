# 🔍 HƯỚNG DẪN DEBUG & TEST DASHBOARD

## Vấn đề: "Không tìm thấy chuyến bay phù hợp"

### 📋 Logic tìm kiếm hiện tại:

Hệ thống lọc chuyến bay theo 4 tiêu chí:

1. **Điểm đi** = Airport ID bạn chọn
2. **Điểm đến** = Airport ID bạn chọn  
3. **Ngày bay** >= 00:00:00 và < 24:00:00 của ngày đó
4. **Status** = "AVAILABLE" hoặc "SCHEDULED"

---

## 🧪 CÁCH TEST:

### Bước 1: Chạy ứng dụng
```bash
.\mvnw.cmd spring-boot:run
```

### Bước 2: Truy cập dashboard
```
http://localhost:8080/dashboard
```

### Bước 3: Kiểm tra log khi search
Khi bạn search chuyến bay, terminal sẽ hiển thị:

```
========== FLIGHT SEARCH DEBUG ==========
Departure Airport ID: APORT-001
Arrival Airport ID: APORT-002
Departure Date: 2025-11-04
Trip Type: oneWay
Search Date Range: 2025-11-04T00:00 to 2025-11-05T00:00
Total flights in DB: 1
Flight: FL-001 | From: Noi Bai International (APORT-001) | To: Tan Son Nhat (APORT-002) | Date: 2025-11-04T10:30 | Status: SCHEDULED
Found 1 matching flights
========================================
```

---

## 🔍 DEBUG CHECKLIST:

### ✅ Kiểm tra 1: Có chuyến bay trong DB không?
Chạy SQL:
```sql
SELECT * FROM flight;
```

Nếu **KHÔNG CÓ** → Chạy lại app để DataInitializer tạo data mẫu

### ✅ Kiểm tra 2: Airport ID có đúng không?
Log sẽ hiện:
```
Departure Airport ID: APORT-001
Arrival Airport ID: APORT-002
```

So sánh với data trong DB:
```sql
SELECT airport_id, code, name FROM airport;
```

### ✅ Kiểm tra 3: Ngày bay có khớp không?
Từ DataInitializer, chuyến bay mẫu được tạo với:
```java
flight.setStartedTime(LocalDateTime.now().plusDays(1));
```

Nghĩa là: **Ngày mai** (4/11/2025)

→ Bạn phải chọn ngày **04/11/2025** mới tìm thấy!

### ✅ Kiểm tra 4: Status có đúng không?
Log sẽ hiện:
```
Status: SCHEDULED
```

Code filter chấp nhận: "AVAILABLE" hoặc "SCHEDULED" ✅

---

## 🎯 VÍ DỤ TEST THÀNH CÔNG:

### Scenario 1: Tìm chuyến bay mẫu từ DataInitializer

1. **Chọn điểm đi**: `Noi Bai International (HNO) - Hanoi`
2. **Chọn điểm đến**: `Tan Son Nhat (SGN) - Ho Chi Minh City`
3. **Chọn ngày**: `04/11/2025` (ngày mai)
4. Click **"Tìm chuyến bay"**

→ Kết quả: Tìm thấy chuyến bay **FL-001**

---

## 🐛 NẾU VẪN KHÔNG TÌM THẤY:

### Giải pháp 1: Xem log trong terminal
Log sẽ cho biết:
- Có bao nhiêu chuyến bay trong DB?
- Airport ID có khớp không?
- Ngày giờ có đúng không?

### Giải pháp 2: Xóa table `seat` để DataInitializer chạy lại
```sql
DELETE FROM seat;
```

Sau đó restart app → DataInitializer sẽ tạo lại data mẫu

### Giải pháp 3: Insert thủ công chuyến bay test
```sql
-- Thêm chuyến bay test cho hôm nay
INSERT INTO flight (flight_id, airplane_id, flight_routes_id, base_price, started_time, ended_time, status)
VALUES ('FL-TEST', 'AP-001', 'FR-001', 1500000, '2025-11-03 10:00:00', '2025-11-03 12:15:00', 'AVAILABLE');
```

---

## 📊 KẾT QUẢ LOG MẪU KHI THÀNH CÔNG:

```
========== FLIGHT SEARCH DEBUG ==========
Departure Airport ID: APORT-001
Arrival Airport ID: APORT-002
Departure Date: 2025-11-04
Trip Type: oneWay
Search Date Range: 2025-11-04T00:00 to 2025-11-05T00:00
Total flights in DB: 1
Flight: FL-001 | From: Noi Bai International (APORT-001) | To: Tan Son Nhat (APORT-002) | Date: 2025-11-04T10:30 | Status: SCHEDULED
Found 1 matching flights  ← ✅ THÀNH CÔNG!
========================================
```

---

## 💡 LƯU Ý:

1. **Ngày hôm nay**: 03/11/2025
2. **Chuyến bay mẫu**: Bay vào **04/11/2025** (ngày mai)
3. **Nếu chọn ngày 03/11**: Không tìm thấy vì chuyến bay là ngày mai
4. **Nếu dropdown trống**: DB chưa có airports → Chạy lại app

---

## 🚀 NEXT STEPS:

Sau khi tìm thấy chuyến bay thành công:
1. Click nút **"Đặt vé"** 
2. Sẽ redirect sang `/booking/passenger-info?flightId=FL-001`
3. Hoàn tất flow booking!
ồi bro