package com.americobarber.service.impl;

import com.americobarber.dto.request.UpdatePixKeyRequest;
import com.americobarber.dto.response.PaymentSettingsResponse;
import com.americobarber.entity.BusinessSettings;
import com.americobarber.exception.BusinessException;
import com.americobarber.repository.BusinessSettingsRepository;
import com.americobarber.service.PaymentSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PaymentSettingsServiceImpl implements PaymentSettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final BusinessSettingsRepository settingsRepository;

    @Override
    @Transactional(readOnly = true)
    public PaymentSettingsResponse getSettings() {
        BusinessSettings settings = getOrCreateSettings();
        return toResponse(settings);
    }

    @Override
    @Transactional
    public PaymentSettingsResponse updatePixKey(UpdatePixKeyRequest request) {
        String pixKey = request.getPixKey() != null ? request.getPixKey().trim() : "";
        if (pixKey.isBlank()) {
            throw new BusinessException("Chave PIX é obrigatória");
        }

        BusinessSettings settings = getOrCreateSettings();
        settings.setPixKey(pixKey);
        settings.setUpdatedAt(Instant.now());
        settings = settingsRepository.save(settings);
        return toResponse(settings);
    }

    private BusinessSettings getOrCreateSettings() {
        return settingsRepository.findById(SETTINGS_ID)
                .orElseGet(() -> settingsRepository.save(BusinessSettings.builder()
                        .id(SETTINGS_ID)
                        .pixKey("89c0800d-0bf4-426b-a9f1-39bea0acdcea")
                        .updatedAt(Instant.now())
                        .build()));
    }

    private PaymentSettingsResponse toResponse(BusinessSettings settings) {
        return PaymentSettingsResponse.builder()
                .pixKey(settings.getPixKey())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
