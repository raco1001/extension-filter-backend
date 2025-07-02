package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FixedExtensionDto {
    private String extension;
    private String displayName;
    private boolean blocked;
} 