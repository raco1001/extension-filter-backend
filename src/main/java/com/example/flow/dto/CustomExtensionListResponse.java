package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class CustomExtensionListResponse {
    private int count;
    private int max;
    private List<CustomExtensionDto> extensions;
} 