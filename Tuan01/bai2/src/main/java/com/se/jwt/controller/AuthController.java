package com.se.jwt.controller;

import com.se.jwt.dto.LoginRequest;
import com.se.jwt.dto.LoginResponse;
import com.se.jwt.dto.RefreshTokenRequest;
import com.se.jwt.service.AuthService;
import com.se.jwt.util.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtUtil jwtUtil;
    @PostMapping("/login")
    public LoginResponse login(
             @RequestBody LoginRequest request
    ) {
        return authService.login(request);
    }

    @PostMapping("/refresh-token")
    public LoginResponse refresh(@RequestBody RefreshTokenRequest request) {
            return  authService.refreshToken(request.getRefreshToken());
    }

}
