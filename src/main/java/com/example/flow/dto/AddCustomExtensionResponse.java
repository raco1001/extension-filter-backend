package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class AddCustomExtensionResponse {
    private boolean success;      
    private UUID id;             
    private String extension;    
    private String message;      
} 