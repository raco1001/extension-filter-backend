package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class CustomExtensionDto {
    private UUID id;
    private String extension;
} 