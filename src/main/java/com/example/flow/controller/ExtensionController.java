package com.example.flow.controller;

import com.example.flow.dto.*;
import com.example.flow.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {
    private final ExtensionService extensionService;

    private UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() != null) {
            return UUID.fromString(authentication.getName());
        }
        throw new IllegalStateException("인증되지 않은 사용자입니다.");
    }


    @GetMapping
    public ResponseEntity<UserExtensionsResponse> getExtensions() {
        UUID userId = getCurrentUserId();
        return ResponseEntity.ok(extensionService.getExtensions(userId));
    }

    @PostMapping("/fixed/{id}")
    public ResponseEntity<UpdateFixedExtensionResponse> updateFixedExtension(@PathVariable UUID id, @RequestBody UpdateFixedExtensionRequest req) {
        UUID userId = getCurrentUserId();
        UpdateFixedExtensionResponse response = extensionService.updateFixedExtension(userId, id, req.isBlocked());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/custom")
    public ResponseEntity<AddCustomExtensionResponse> addCustomExtension(@RequestBody AddCustomExtensionRequest req) {
        UUID userId = getCurrentUserId();
        AddCustomExtensionResponse response = extensionService.addCustomExtension(userId, req.getExtension());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/custom/{id}")
    public ResponseEntity<DeleteCustomExtensionResponse> removeCustomExtension(@PathVariable UUID id) {
        UUID userId = getCurrentUserId();
        DeleteCustomExtensionResponse response = extensionService.removeCustomExtensionById(userId, id);
        return ResponseEntity.ok(response);
    }
} 