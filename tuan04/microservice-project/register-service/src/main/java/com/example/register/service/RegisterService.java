package com.example.register.service;

import com.example.register.entity.User;
import com.example.register.model.RegisterRequest;
import com.example.register.model.RegisterResponse;
import com.example.register.model.UserInfo;
import com.example.register.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public RegisterService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository  = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponse register(RegisterRequest request) {
        log.info("Đang xử lý đăng ký cho username: {}", request.getUsername());

        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username '{}' đã tồn tại", request.getUsername());
            return RegisterResponse.builder()
                    .success(false)
                    .message("Username '" + request.getUsername() + "' đã được sử dụng")
                    .build();
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email '{}' đã tồn tại", request.getEmail());
            return RegisterResponse.builder()
                    .success(false)
                    .message("Email '" + request.getEmail() + "' đã được sử dụng")
                    .build();
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .fullName(request.getFullName())
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Đăng ký thành công cho user ID: {}", savedUser.getId());

        return RegisterResponse.builder()
                .success(true)
                .message("Đăng ký thành công! Chào mừng " + savedUser.getUsername())
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    public UserInfo getUserByUsername(String username) {
        log.info("[Internal] Login Service yêu cầu thông tin user: {}", username);

        return userRepository.findByUsername(username)
                .map(user -> UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .build())
                .orElse(null);
    }
}
