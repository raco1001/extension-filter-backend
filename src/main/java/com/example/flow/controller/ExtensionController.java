package com.example.flow.controller;

import com.example.flow.dto.*;
import com.example.flow.service.ExtensionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/extensions")
@RequiredArgsConstructor
public class ExtensionController {
    private final ExtensionService extensionService;

    @GetMapping("/fixed")
    public List<FixedExtensionDto> getFixedExtensions(@RequestHeader("X-User-Id") UUID userId) {
        return extensionService.getFixedExtensions(userId);
    }

    @PutMapping("/fixed")
    public ResponseEntity<Void> updateFixedExtension(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody UpdateFixedExtensionRequest req
    ) {
        extensionService.updateFixedExtension(userId, req.getExtension(), req.isBlocked());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/custom")
    public CustomExtensionListResponse getCustomExtensions(@RequestHeader("X-User-Id") UUID userId) {
        return extensionService.getCustomExtensions(userId);
    }

    @PostMapping("/custom")
    public ResponseEntity<Void> addCustomExtension(
            @RequestHeader("X-User-Id") UUID userId,
            @RequestBody AddCustomExtensionRequest req
    ) {
        extensionService.addCustomExtension(userId, req.getExtension());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/custom/{extension}")
    public ResponseEntity<Void> removeCustomExtension(
            @RequestHeader("X-User-Id") UUID userId,
            @PathVariable String extension
    ) {
        extensionService.removeCustomExtension(userId, extension);
        return ResponseEntity.ok().build();
    }
} 