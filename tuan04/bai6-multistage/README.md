# Bài 6 - Multi-stage Build

Dùng với dự án React:
- Stage 1: node:18 build source code
- Stage 2: node:18-alpine chạy bản đã build (image nhỏ hơn)

docker build -t node-multistage .
docker run -p 3000:3000 node-multistage
