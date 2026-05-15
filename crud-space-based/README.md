# Hướng dẫn Chạy Dự án CRUD - Space-Based Architecture (SBA)

Dự án này triển khai mô hình Kiến trúc Dựa trên Không gian (SBA) sử dụng **Spring Boot**, **Kafka**, **Redis**, và **MySQL**.

## Kiến trúc Hệ thống
1.  **Product PU (Processing Unit)**: Nhận request từ người dùng, ghi dữ liệu vào **Redis** (Space) để phản hồi nhanh và gửi thông báo thay đổi tới **Kafka**.
2.  **Product Persistence Service**: Lắng nghe các sự kiện từ **Kafka** và đồng bộ dữ liệu vào **MySQL** (Long-term Storage).
3.  **Redis**: In-Memory Data Grid đóng vai trò là "Space".
4.  **Kafka**: Message Grid dùng để truyền tải dữ liệu bất đồng bộ giữa PU và Persistence Layer.

---

## Yêu cầu Hệ thống
*   Docker & Docker Compose
*   Java 17+ (nếu muốn chạy local không qua Docker)
*   Maven 3.8+ (nếu muốn build local)

---

## Cách Chạy Dự án bằng Docker

### 1. Khởi động toàn bộ hệ thống
Mở terminal tại thư mục gốc của dự án và chạy lệnh:
```bash
docker-compose up --build
```
Lệnh này sẽ tự động:
*   Khởi chạy Redis, MySQL, Zookeeper, Kafka.
*   Build source code Java và khởi chạy 2 service Spring Boot.

### 2. Kiểm tra trạng thái
Đợi vài phút để các service khởi động hoàn tất. Bạn có thể kiểm tra danh sách container đang chạy:
```bash
docker ps
```

---

## Kiểm tra Tính năng (CRUD)

Bạn có thể sử dụng Postman hoặc `curl` để kiểm tra:

### 1. Tạo mới một sản phẩm (Create)
**Endpoint**: `POST http://localhost:8081/products`
**Body (JSON)**:
```json
    
```
*   **Kết quả**: Dữ liệu sẽ được lưu vào Redis ngay lập tức và sau đó vài giây sẽ xuất hiện trong MySQL.

### 2. Lấy danh sách sản phẩm (Read)
**Endpoint**: `GET http://localhost:8081/products`
*   Dữ liệu được lấy trực tiếp từ Redis.

### 3. Cập nhật sản phẩm (Update)
**Endpoint**: `PUT http://localhost:8081/products/p1`
**Body (JSON)**:
```json
{
  "name": "iPhone 15 Pro Max",
  "price": 1199.0
}
```

### 4. Xóa sản phẩm (Delete)
**Endpoint**: `DELETE http://localhost:8081/products/p1`

---

## Truy cập Dữ liệu trực tiếp

### Kiểm tra trong Redis
```bash
docker exec -it <redis_container_id> redis-cli
HGETALL PRODUCTS
```

### Kiểm tra bằng DBeaver / Công cụ quản lý DB
Nếu bạn sử dụng DBeaver, hãy tạo connection mới với thông số:
*   **Host**: `localhost`
*   **Port**: `3306`
*   **Database**: `sba_db`
*   **Username**: `root`
*   **Password**: `root`

---

## Ghi chú
*   **Cấu hình**: Các tham số kết nối được định nghĩa trong `docker-compose.yml` và `application.properties`.
*   **Tính nhất quán**: Vì đây là mô hình bất đồng bộ (Eventually Consistent), dữ liệu trong MySQL có thể chậm hơn Redis một vài giây.
