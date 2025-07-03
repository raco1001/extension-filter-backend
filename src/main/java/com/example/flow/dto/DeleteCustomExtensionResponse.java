package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;

@Data
@AllArgsConstructor
public class DeleteCustomExtensionResponse {
    private boolean success;     
    private UUID extensionId;      
    private String deletedExtension; 
} 