package com.publish.service;

import com.publish.model.User;

import com.publish.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

// ── Business Logic Layer: Xác thực quyền ─────────────────────────
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ── Đăng ký ──────────────────────────────────────────────────

    public User register(String username, String email, String password, User.Role role) {
        if (userRepository.existsByUsername(username))
            throw new IllegalArgumentException("Username đã tồn tại");
        if (userRepository.existsByEmail(email))
            throw new IllegalArgumentException("Email đã tồn tại");

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(bcrypt.encode(password));
        user.setRole(role != null ? role : User.Role.WRITER);
        return userRepository.save(user);
    }

    // ── Đăng nhập ────────────────────────────────────────────────

    public User login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Username không tồn tại"));

        if (!bcrypt.matches(password, user.getPasswordHash()))
            throw new IllegalArgumentException("Mật khẩu không đúng");

        return user;
    }

    // ── Kiểm tra quyền (token: val trò) ──────────────────────────

    public Map<String, Object> getUserInfo(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));

        return Map.of(
                "username", user.getUsername(),
                "email", user.getEmail(),
                "role", user.getRole().name(),
                "canPublish", canPublish(user),
                "canManageMedia", canManageMedia(user),
                "canManageUsers", canManageUsers(user)
        );
    }

    public boolean canPublish(User user) {
        return user.getRole() == User.Role.ADMIN ||
                user.getRole() == User.Role.EDITOR;
    }

    public boolean canManageMedia(User user) {
        return user.getRole() == User.Role.ADMIN ||
                user.getRole() == User.Role.EDITOR;
    }

    public boolean canManageUsers(User user) {
        return user.getRole() == User.Role.ADMIN;
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
    }

    public User updateRole(Long userId, User.Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User không tồn tại"));
        user.setRole(newRole);
        return userRepository.save(user);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
