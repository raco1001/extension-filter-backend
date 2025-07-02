package com.example.flow.dto;

import lombok.Data;

@Data
public class UpdateFixedExtensionRequest {
    private String extension;
    private boolean blocked;
} 