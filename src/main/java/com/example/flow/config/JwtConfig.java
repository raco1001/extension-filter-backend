package com.example.flow.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
@Component
@Validated
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    @NotBlank(message = "JWT secret key is required")
    @Size(min = 32, message = "JWT secret key must be at least 32 characters long for security")
    private String secret = "flow-extension-blocker-super-secret-jwt-key-2024-production-ready-very-long-and-secure";
    
    @NotNull(message = "JWT expiration time is required")
    @Min(value = 60000, message = "JWT expiration must be at least 1 minute (60000ms)")
    private long expiration = 86400000; 
    
    @NotBlank(message = "JWT issuer is required")
    private String issuer = "flow-extension-blocker";
} 