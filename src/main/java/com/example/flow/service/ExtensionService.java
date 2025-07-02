package com.example.flow.service;

import com.example.flow.domain.*;
import com.example.flow.dto.*;
import com.example.flow.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExtensionService {
    private final FixedBlockedExtensionRepository fixedRepo;
    private final UserRepository userRepo;
    private final UserFixedBlockedExtensionRepository userFixedRepo;
    private final UserCustomBlockedExtensionRepository userCustomRepo;
    private final SystemSettingRepository systemSettingRepo;

    public List<FixedExtensionDto> getFixedExtensions(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();
        List<FixedBlockedExtension> all = fixedRepo.findAll();
        Map<UUID, UserFixedBlockedExtension> userMap = userFixedRepo.findByUser(user).stream()
                .collect(Collectors.toMap(
                        u -> u.getExtension().getId(),
                        u -> u
                ));
        return all.stream().map(fixed -> {
            boolean blocked = userMap.getOrDefault(fixed.getId(), null) != null
                    ? userMap.get(fixed.getId()).isBlocked()
                    : fixed.isBlocked();
            return new FixedExtensionDto(fixed.getExtension(), fixed.getDisplayName(), blocked);
        }).collect(Collectors.toList());
    }

    @Transactional
    public void updateFixedExtension(UUID userId, String extension, boolean blocked) {
        User user = userRepo.findById(userId).orElseThrow();
        FixedBlockedExtension fixed = fixedRepo.findByExtension(extension)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 확장자"));
        UserFixedBlockedExtension userFixed = userFixedRepo.findByUserAndExtension(user, fixed)
                .orElse(null);
        if (userFixed == null) {
            userFixed = new UserFixedBlockedExtension();
            userFixed.setId(UUID.randomUUID());
            userFixed.setUser(user);
            userFixed.setExtension(fixed);
        }
        userFixed.setBlocked(blocked);
        userFixedRepo.save(userFixed);
    }

    public CustomExtensionListResponse getCustomExtensions(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();
        List<UserCustomBlockedExtension> list = userCustomRepo.findByUser(user);
        int max = getMaxCustomExtension();
        List<CustomExtensionDto> dtos = list.stream()
                .map(e -> new CustomExtensionDto(e.getExtension()))
                .collect(Collectors.toList());
        return new CustomExtensionListResponse(dtos.size(), max, dtos);
    }

    @Transactional
    public void addCustomExtension(UUID userId, String extension) {
        User user = userRepo.findById(userId).orElseThrow();
        extension = extension.trim().toLowerCase();
        if (extension.length() == 0 || extension.length() > 20)
            throw new IllegalArgumentException("확장자 길이 오류");
        if (userCustomRepo.countByUser(user) >= getMaxCustomExtension())
            throw new IllegalStateException("최대 개수 초과");
        if (userCustomRepo.findByUserAndExtension(user, extension).isPresent())
            throw new IllegalArgumentException("중복 확장자");
        if (fixedRepo.findByExtension(extension).isPresent())
            throw new IllegalArgumentException("고정 확장자와 중복");
        UserCustomBlockedExtension entity = new UserCustomBlockedExtension();
        entity.setId(UUID.randomUUID());
        entity.setUser(user);
        entity.setExtension(extension);
        userCustomRepo.save(entity);
    }

    @Transactional
    public void removeCustomExtension(UUID userId, String extension) {
        User user = userRepo.findById(userId).orElseThrow();
        UserCustomBlockedExtension entity = userCustomRepo.findByUserAndExtension(user, extension)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않음"));
        userCustomRepo.delete(entity);
    }

    private int getMaxCustomExtension() {
        return systemSettingRepo.findBySettingKey("max_custom_extensions")
                .map(s -> Integer.parseInt(s.getSettingValue()))
                .orElse(200);
    }
} 