FROM node:18-alpine
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=0 /app/dist /usr/share/nginx/html
# Custom nginx config to handle SPA routing if needed
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
