# Movie Ticket System (Event-Driven Architecture)

Hệ thống đặt vé xem phim sử dụng Spring Boot (Microservices), ReactJS và Kafka.

## Cấu trúc Project
- `services/user-service`: Cổng đăng ký/đăng nhập (Port 8081).
- `services/movie-service`: Quản lý danh sách phim (Port 8082).
- `services/booking-service`: Xử lý đặt vé (Port 8083).
- `services/payment-service`: Xử lý thanh toán và thông báo (Port 8084).
- `frontend`: Giao diện ReactJS (Port 5173).

## Hướng dẫn chạy hệ thống

### 1. Khởi động Kafka
Yêu cầu đã cài đặt Docker. Chạy lệnh sau tại thư mục gốc:
```bash
docker-compose up -d
```

### 2. Chạy các Backend Services
Mở 4 terminal riêng biệt và chạy lệnh sau cho từng service:
```bash
cd services/user-service && ./mvnw spring-boot:run
cd services/movie-service && ./mvnw spring-boot:run
cd services/booking-service && ./mvnw spring-boot:run
cd services/payment-service && ./mvnw spring-boot:run
```
*(Lưu ý: Nếu chưa có `mvnw`, hãy đảm bảo đã cài đặt Maven và dùng `mvn spring-boot:run`)*

### 3. Chạy Frontend
```bash
cd frontend
npm install
npm run dev
```

## Kịch bản Test
1. Truy cập giao diện (thường là `http://localhost:5173`).
2. Đăng nhập bằng `admin` / `password` (hoặc đăng ký user mới).
3. Tại trang **Movies**, chọn một phim và nhấn **Book Now**.
4. Chuyển sang tab **My Bookings**.
5. Bạn sẽ thấy đơn hàng ở trạng thái `PENDING`. Sau vài giây (giả lập thanh toán), trạng thái sẽ cập nhật thành `SUCCESS` hoặc `FAILED` (dựa trên sự kiện từ Kafka).
6. Kiểm tra log của `payment-service` để thấy thông báo `NOTIFICATION`.
