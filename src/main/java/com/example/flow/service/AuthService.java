package com.example.flow.service;

import com.example.flow.domain.User;
import com.example.flow.dto.LoginRequest;
import com.example.flow.dto.LoginResponse;
import com.example.flow.repository.UserRepository;
import com.example.flow.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다."));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 잘못되었습니다.");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail());
        
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getUsername()
        );
    }

    public User getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new IllegalArgumentException("만료된 토큰입니다.");
        }

        UUID userId = jwtUtil.getUserIdFromToken(token);
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
    }
} 