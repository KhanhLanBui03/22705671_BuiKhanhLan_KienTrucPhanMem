# Hệ Thống Flash Sale - Kiến Trúc Space-Based (SBA)

Dự án này mô phỏng một hệ thống Flash Sale hiệu năng cao, được xây dựng dựa trên các nguyên lý của **Kiến trúc dựa trên không gian (Space-Based Architecture - SBA)**.

## Kiến Trúc Hệ Thống
- **Data Grid**: Sử dụng **Redis** (Bộ nhớ dùng chung cho tất cả các đơn vị xử lý - Processing Units).
- **Processing Units (PUs) - Các Đơn Vị Xử Lý**:
  - **PU1 (Product)**: Cổng 8081. Quản lý danh mục sản phẩm và nạp dữ liệu mẫu vào Redis.
  - **PU2 (Cart)**: Cổng 8082. Quản lý giỏ hàng tạm thời của người dùng trên Data Grid.
  - **PU3 (Order)**: Cổng 8083. Điều phối quy trình thanh toán (checkout) và trừ tồn kho.
  - **PU4 (Inventory)**: Cổng 8084. Quản lý tồn kho thời gian thực với các thao tác nguyên tử (atomic) bằng Lua script.
- **Frontend**: React + Vite chạy trên Cổng 3000.

## Cách Chạy Hệ Thống

### Yêu Cầu Tiên Quyết
- Đã cài đặt **Docker** và **Docker Compose**.

### Các Bước Thực Hiện
1. Mở terminal tại thư mục gốc của dự án.
2. Chạy lệnh sau để khởi động tất cả các dịch vụ:
   ```bash
   docker-compose up --build
   ```
3. Sau khi các dịch vụ đã khởi động xong, mở trình duyệt và truy cập:
   `http://localhost:3000`

### Demo Khả Năng Chịu Tải Cao (High-Concurrency)
Inventory PU sử dụng một đoạn mã **Lua script** (`services/inventory-pu/index.js`) để đảm bảo việc trừ tồn kho là nguyên tử (atomic). Điều này giúp ngăn chặn tình trạng "bán quá số lượng" (overselling) trong các đợt flash sale, ngay cả khi có hàng nghìn yêu cầu cùng lúc.

## Các Tính Năng Nổi Bật
- **Giao diện Cao cấp (Premium UI)**: Chế độ Dark mode với phong cách Glassmorphism và hiệu ứng chuyển động mượt mà.
- **Tồn kho Thời gian thực**: Số lượng hàng trong kho được cập nhật ngay lập tức trên UI sau mỗi đơn hàng thành công.
- **Xử lý Nguyên tử**: Không bị nghẽn cổ chai tại database truyền thống; mọi xử lý đều diễn ra trực tiếp trên Data Grid (Redis).
- **Thiết kế Low-Latency**: Phản hồi kết quả ngay lập tức cho người dùng mà không cần chờ đợi ghi xuống Database chậm chạp.
