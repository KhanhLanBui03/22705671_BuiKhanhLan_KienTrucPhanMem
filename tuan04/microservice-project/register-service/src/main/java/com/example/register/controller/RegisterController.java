package com.example.register.controller;

import com.example.register.model.RegisterRequest;
import com.example.register.model.RegisterResponse;
import com.example.register.model.UserInfo;
import com.example.register.service.RegisterService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/register")
public class RegisterController {

    private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

    private final RegisterService registerService;

    public RegisterController(RegisterService registerService) {
        this.registerService = registerService;
    }

    @PostMapping
    public ResponseEntity<RegisterResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Nhận yêu cầu đăng ký từ: {}", request.getUsername());
        RegisterResponse response = registerService.register(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    // ⬇️ Thêm tên tường minh vào @PathVariable("username")
    @GetMapping("/internal/user/{username}")
    public ResponseEntity<UserInfo> getUserByUsername(@PathVariable("username") String username) {
        log.info("[Internal] Nhận yêu cầu lấy thông tin user: {}", username);
        UserInfo userInfo = registerService.getUserByUsername(username);
        if (userInfo != null) {
            return ResponseEntity.ok(userInfo);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Register Service is running OK");
    }
}
