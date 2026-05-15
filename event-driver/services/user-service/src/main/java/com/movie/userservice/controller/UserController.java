package com.movie.userservice.controller;

import com.movie.userservice.service.UserService;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public String register(@RequestBody UserRequest request) {
        return userService.register(request.getUsername(), request.getPassword());
    }

    @PostMapping("/login")
    public String login(@RequestBody UserRequest request) {
        if (userService.login(request.getUsername(), request.getPassword())) {
            return "Login successful";
        }
        // Fallback for admin demo
        if ("admin".equals(request.getUsername()) && "password".equals(request.getPassword())) {
            return "Login successful";
        }
        return "Login failed";
    }
}

@Data
class UserRequest {
    private String username;
    private String password;
}
