package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateFixedExtensionResponse {
    private boolean changed;  
    private boolean blocked;  
}   