package com.publish.controller;

import com.publish.model.User;
import com.publish.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// ── Presentation Layer: GET /user ─────────────────────────────────
@RestController
@RequestMapping("/api")
public class UserController {

    private final AuthService authService;

    public UserController(AuthService authService) {
        this.authService = authService;
    }

    // POST /api/auth/register
    @PostMapping("/auth/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        User.Role role = null;
        if (body.get("role") != null) {
            try { role = User.Role.valueOf(body.get("role")); } catch (Exception ignored) {}
        }
        User user = authService.register(
            body.get("username"),
            body.get("email"),
            body.get("password"),
            role
        );
        return ResponseEntity.ok(Map.of(
            "username", user.getUsername(),
            "email",    user.getEmail(),
            "role",     user.getRole().name()
        ));
    }

    // POST /api/auth/login
    @PostMapping("/auth/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) {
        User user = authService.login(body.get("username"), body.get("password"));
        return ResponseEntity.ok(Map.of(
            "username",       user.getUsername(),
            "email",          user.getEmail(),
            "role",           user.getRole().name(),
            "canPublish",     authService.canPublish(user),
            "canManageMedia", authService.canManageMedia(user),
            "canManageUsers", authService.canManageUsers(user)
        ));
    }

    // GET /api/user — thông tin + quyền của user hiện tại
    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getUserInfo(@RequestHeader("X-Username") String username) {
        return ResponseEntity.ok(authService.getUserInfo(username));
    }

    // GET /api/users — danh sách tất cả user (ADMIN)
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers(@RequestHeader("X-Username") String username) {
        User requester = authService.findByUsername(username);
        if (!authService.canManageUsers(requester))
            return ResponseEntity.status(403).build();
        return ResponseEntity.ok(authService.getAllUsers());
    }

    // PUT /api/users/{id}/role — đổi vai trò (ADMIN)
    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> updateRole(@PathVariable Long id,
                                                           @RequestBody Map<String, String> body,
                                                           @RequestHeader("X-Username") String username) {
        User requester = authService.findByUsername(username);
        if (!authService.canManageUsers(requester))
            return ResponseEntity.status(403).body(Map.of("error", "Không có quyền"));

        User.Role newRole = User.Role.valueOf(body.get("role"));
        User updated = authService.updateRole(id, newRole);
        return ResponseEntity.ok(Map.of(
            "id",       updated.getId(),
            "username", updated.getUsername(),
            "role",     updated.getRole().name()
        ));
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(RuntimeException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
