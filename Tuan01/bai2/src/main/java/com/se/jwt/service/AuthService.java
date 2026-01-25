package com.se.jwt.service;

import com.se.jwt.dto.LoginRequest;
import com.se.jwt.dto.LoginResponse;
import com.se.jwt.model.RefreshToken;
import com.se.jwt.model.User;
import com.se.jwt.repository.RefreshTokenRepository;
import com.se.jwt.repository.RoleRepository;
import com.se.jwt.repository.UserRepository;
import com.se.jwt.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        refreshTokenRepository.deleteByUser(user);
        refreshTokenRepository.flush();
        String accessToken = jwtUtil.generateAccessToken(user);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(jwtUtil.generateRefreshToken());
        refreshToken.setUser(user);
        refreshToken.setExpiryDate(
                LocalDateTime.now().plusDays(30)
        );

        refreshTokenRepository.save(refreshToken);

        return new LoginResponse(accessToken, refreshToken.getToken());
    }

    public LoginResponse refreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "Invalid refresh token"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(token);
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Refresh token expired");
        }

        String newAccessToken = jwtUtil.generateAccessToken(token.getUser());

        return new LoginResponse(newAccessToken, token.getToken());
    }

}
