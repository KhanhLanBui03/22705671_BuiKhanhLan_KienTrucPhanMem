package com.example.login.service;

import com.example.login.model.LoginRequest;
import com.example.login.model.LoginResponse;
import com.example.login.model.UserInfo;
import com.example.login.util.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class LoginService {

    private static final Logger log = LoggerFactory.getLogger(LoginService.class);

    private final RestTemplate restTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Value("${register.service.url}")
    private String registerServiceUrl;

    public LoginService(RestTemplate restTemplate,
                        BCryptPasswordEncoder passwordEncoder,
                        JwtUtil jwtUtil) {
        this.restTemplate    = restTemplate;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil         = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        log.info("Xử lý đăng nhập cho username: {}", request.getUsername());

        // ── Bước 1: Gọi Register Service qua RestTemplate ──────────────
        UserInfo userInfo = null;
        try {
            String url = registerServiceUrl + "/api/register/internal/user/" + request.getUsername();
            log.info("Gọi Register Service tại: {}", url);

            ResponseEntity<UserInfo> response = restTemplate.getForEntity(url, UserInfo.class);
            userInfo = response.getBody();
            log.info("Register Service trả về status: {}", response.getStatusCode());

        } catch (HttpClientErrorException e) {
            // 404 Not Found hoặc các lỗi 4xx khác
            log.warn("Register Service trả lỗi HTTP {}: user '{}' không tồn tại",
                    e.getStatusCode(), request.getUsername());
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản không tồn tại")
                    .build();

        } catch (RestClientException e) {
            // Lỗi kết nối (connection refused, timeout...)
            log.error("Không thể kết nối Register Service: {}", e.getMessage());
            return LoginResponse.builder()
                    .success(false)
                    .message("Không thể kết nối đến Register Service. Vui lòng thử lại sau.")
                    .build();

        } catch (Exception e) {
            log.error("Lỗi không xác định khi gọi Register Service", e);
            return LoginResponse.builder()
                    .success(false)
                    .message("Lỗi hệ thống: " + e.getMessage())
                    .build();
        }

        // ── Bước 2: Kiểm tra userInfo null ─────────────────────────────
        if (userInfo == null || userInfo.getUsername() == null) {
            log.warn("Register Service trả về dữ liệu rỗng cho user: {}", request.getUsername());
            return LoginResponse.builder()
                    .success(false)
                    .message("Tài khoản không tồn tại")
                    .build();
        }

        // ── Bước 3: Xác thực password với BCrypt ───────────────────────
        log.info("Đang xác thực password cho user: {}", userInfo.getUsername());
        if (!passwordEncoder.matches(request.getPassword(), userInfo.getPassword())) {
            log.warn("Sai mật khẩu cho user: {}", request.getUsername());
            return LoginResponse.builder()
                    .success(false)
                    .message("Mật khẩu không chính xác")
                    .build();
        }

        // ── Bước 4: Tạo JWT token ───────────────────────────────────────
        String token = jwtUtil.generateToken(userInfo.getUsername(), userInfo.getEmail());
        log.info("Đăng nhập thành công, đã cấp JWT cho user: {}", userInfo.getUsername());

        return LoginResponse.builder()
                .success(true)
                .message("Đăng nhập thành công!")
                .token(token)
                .username(userInfo.getUsername())
                .email(userInfo.getEmail())
                .build();
    }
}
