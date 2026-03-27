# Stage 1: Build
FROM node:18 AS builder

WORKDIR /app
COPY package*.json .
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Run
FROM node:18-alpine

RUN npm install -g serve
WORKDIR /app
COPY --from=builder /app/build .

EXPOSE 3000
CMD ["serve", "-s", ".", "-l", "3000"]
