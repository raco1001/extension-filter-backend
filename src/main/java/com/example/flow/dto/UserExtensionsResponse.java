package com.example.flow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class UserExtensionsResponse {
    private List<FixedExtensionDto> fixed;
    private List<CustomExtensionDto> custom;
}