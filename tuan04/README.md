# Docker Exercises - Phần 2: Thao tác với Dockerfile

## Danh sách bài tập

| Bài | Tên | Port | Lệnh build & run |
|-----|-----|------|-----------------|
| 1 | Node.js | 3000 | `docker build -t nodejs-app . && docker run -p 3000:3000 nodejs-app` |
| 2 | Python Flask | 5000 | `docker build -t flask-app . && docker run -p 5000:5000 flask-app` |
| 3 | React App | 3000 | `docker build -t react-app . && docker run -p 3000:3000 react-app` |
| 4 | Nginx Static | 8080 | `docker build -t nginx-web . && docker run -p 8080:80 nginx-web` |
| 5 | Go App | 8080 | `docker build -t go-app . && docker run -p 8080:8080 go-app` |
| 6 | Multi-stage | 3000 | `docker build -t node-multistage . && docker run -p 3000:3000 node-multistage` |
| 7 | ENV Python | - | `docker build -t env-app . && docker run env-app` |
| 8 | PostgreSQL | 5432 | `docker build -t custom-postgres . && docker run -p 5432:5432 custom-postgres` |
| 9 | Redis | 6379 | `docker build -t custom-redis . && docker run -p 6379:6379 custom-redis` |
| 10 | PHP Apache | 8080 | `docker build -t php-app . && docker run -p 8080:80 php-app` |

## Lưu ý
- Bài 3 và Bài 6 cần tạo React app trước: `npx create-react-app my-app`
- Bài 7: Override ENV khi chạy: `docker run -e APP_ENV=production env-app`
- Bài 10: Mount volume: `docker run -p 8080:80 -v $(pwd)/src:/var/www/html php-app`
