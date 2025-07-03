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
            boolean blocked = userMap.containsKey(fixed.getId());
            return new FixedExtensionDto(
                fixed.getId(),
                fixed.getExtension(),
                fixed.getDisplayName(),
                blocked
            );
        }).collect(Collectors.toList());
    }

    @Transactional
    public UpdateFixedExtensionResponse updateFixedExtension(UUID userId, UUID extensionId, boolean blocked) {
        User user = userRepo.findById(userId).orElseThrow();
        FixedBlockedExtension fixed = fixedRepo.findById(extensionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 확장자"));
        
        Optional<UserFixedBlockedExtension> userFixedOpt = userFixedRepo.findByUserAndExtension(user, fixed);
        boolean changed = false;
        
        if (blocked) {
            if (userFixedOpt.isEmpty()) {
                UserFixedBlockedExtension userFixed = new UserFixedBlockedExtension();
                userFixed.setId(UUID.randomUUID());
                userFixed.setUser(user);
                userFixed.setExtension(fixed);
                userFixedRepo.save(userFixed);
                changed = true;
            }
        } else {
            if (userFixedOpt.isPresent()) {
                userFixedRepo.delete(userFixedOpt.get());
                changed = true;
            }
        }
        
        return new UpdateFixedExtensionResponse(changed, blocked);
    }

    public CustomExtensionListResponse getCustomExtensions(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();
        List<UserCustomBlockedExtension> list = userCustomRepo.findByUser(user);
        int max = getMaxCustomExtension();
        List<CustomExtensionDto> dtos = list.stream()
                .map(e -> new CustomExtensionDto(e.getId(), e.getExtension()))
                .collect(Collectors.toList());
        return new CustomExtensionListResponse(dtos.size(), max, dtos);
    }

    @Transactional
    public AddCustomExtensionResponse addCustomExtension(UUID userId, String extension) {
        User user = userRepo.findById(userId).orElseThrow();
        
        try {
            extension = extension.trim().toLowerCase();
            if (extension.length() == 0 || extension.length() > 20)
                return new AddCustomExtensionResponse(false, null, extension, "확장자 길이 오류");
            if (userCustomRepo.countByUser(user) >= getMaxCustomExtension())
                return new AddCustomExtensionResponse(false, null, extension, "최대 개수 초과");
            if (userCustomRepo.findByUserAndExtension(user, extension).isPresent())
                return new AddCustomExtensionResponse(false, null, extension, "중복 확장자");
            if (fixedRepo.findByExtension(extension).isPresent())
                return new AddCustomExtensionResponse(false, null, extension, "고정 확장자와 중복");
                
            UserCustomBlockedExtension entity = new UserCustomBlockedExtension();
            UUID newId = UUID.randomUUID();
            entity.setId(newId);
            entity.setUser(user);
            entity.setExtension(extension);
            userCustomRepo.save(entity);
            
            return new AddCustomExtensionResponse(true, newId, extension, "추가 완료");
        } catch (Exception e) {
            return new AddCustomExtensionResponse(false, null, extension, e.getMessage());
        }
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

    @Transactional
    public AddCustomExtensionResponse addCustomExtension(UUID userId, String extension, boolean success) {
        User user = userRepo.findById(userId).orElseThrow();
        
        if (success) {
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
            UUID newId = UUID.randomUUID();
            entity.setId(newId);
            entity.setUser(user);
            entity.setExtension(extension);
            userCustomRepo.save(entity);
            
            return new AddCustomExtensionResponse(true, newId, extension, "추가 완료");
        } else {
            Optional<UserCustomBlockedExtension> entityOpt = userCustomRepo.findByUserAndExtension(user, extension);
            if (entityOpt.isEmpty()) {
                return new AddCustomExtensionResponse(false, null, extension, "존재하지 않음");
            }
            
            if (entityOpt.isEmpty() || !entityOpt.get().getUser().getId().equals(userId)) {
                return new AddCustomExtensionResponse(false, null, null, "존재하지 않음");
            }
            
            UserCustomBlockedExtension entity = entityOpt.get();
            String deletedExtension = entity.getExtension();
            userCustomRepo.delete(entity);
            
            return new AddCustomExtensionResponse(true, null, deletedExtension, "삭제 완료");
        }
    }

    @Transactional
    public AddCustomExtensionResponse addCustomExtensionWithId(UUID userId, UUID extensionId, String extension) {
        User user = userRepo.findById(userId).orElseThrow();
        
        try {
            extension = extension.trim().toLowerCase();
            if (extension.length() == 0 || extension.length() > 20)
                return new AddCustomExtensionResponse(false, null, extension, "확장자 길이 오류");
            if (userCustomRepo.countByUser(user) >= getMaxCustomExtension())
                return new AddCustomExtensionResponse(false, null, extension, "최대 개수 초과");
            if (userCustomRepo.findByUserAndExtension(user, extension).isPresent())
                return new AddCustomExtensionResponse(false, null, extension, "중복 확장자");
            if (fixedRepo.findByExtension(extension).isPresent())
                return new AddCustomExtensionResponse(false, null, extension, "고정 확장자와 중복");
                
            UserCustomBlockedExtension entity = new UserCustomBlockedExtension();
            entity.setId(extensionId);
            entity.setUser(user);
            entity.setExtension(extension);
            userCustomRepo.save(entity);
            
            return new AddCustomExtensionResponse(true, extensionId, extension, "추가 완료");
        } catch (Exception e) {
            return new AddCustomExtensionResponse(false, null, extension, e.getMessage());
        }
    }

    @Transactional
    public DeleteCustomExtensionResponse removeCustomExtensionById(UUID userId, UUID extensionId) {       

        Optional<UserCustomBlockedExtension> entityOpt = userCustomRepo.findById(extensionId);
        if (entityOpt.isEmpty() || !entityOpt.get().getUser().getId().equals(userId)) {
            return new DeleteCustomExtensionResponse(false, null, null);
        }
        
        UserCustomBlockedExtension entity = entityOpt.get();
        String deletedExtension = entity.getExtension();
        userCustomRepo.delete(entity);
        
        return new DeleteCustomExtensionResponse(true, extensionId, deletedExtension);
    }

    public UserExtensionsResponse getExtensions(UUID userId) {
        User user = userRepo.findById(userId).orElseThrow();

        List<FixedBlockedExtension> fixedList = fixedRepo.findAll();
        Map<UUID, UserFixedBlockedExtension> userFixedMap = userFixedRepo.findByUser(user).stream()
            .collect(Collectors.toMap(u -> u.getExtension().getId(), u -> u));
        List<FixedExtensionDto> fixedResult = new ArrayList<>();
        for (FixedBlockedExtension fixed : fixedList) {
            boolean blocked = userFixedMap.containsKey(fixed.getId());
            fixedResult.add(new FixedExtensionDto(
                fixed.getId(),
                fixed.getExtension(),
                fixed.getDisplayName(),
                blocked
            ));
        }

        List<UserCustomBlockedExtension> customList = userCustomRepo.findByUser(user);
        List<CustomExtensionDto> customResult = customList.stream()
            .map(custom -> new CustomExtensionDto(custom.getId(), custom.getExtension()))
            .collect(Collectors.toList());

        return new UserExtensionsResponse(fixedResult, customResult);
    }
} 