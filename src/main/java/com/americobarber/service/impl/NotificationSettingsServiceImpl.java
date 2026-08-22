package com.americobarber.service.impl;

import com.americobarber.dto.request.NotificationSettingsRequest;
import com.americobarber.dto.response.NotificationSettingsResponse;
import com.americobarber.entity.NotificationSettings;
import com.americobarber.repository.NotificationSettingsRepository;
import com.americobarber.service.NotificationSettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class NotificationSettingsServiceImpl implements NotificationSettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final NotificationSettingsRepository settingsRepository;

    @Override
    @Transactional(readOnly = true)
    public NotificationSettingsResponse getSettings() {
        return toResponse(getOrCreateSettings());
    }

    @Override
    @Transactional
    public NotificationSettingsResponse updateSettings(NotificationSettingsRequest request) {
        NotificationSettings settings = getOrCreateSettings();
        settings.setConfirmationEnabled(request.getConfirmationEnabled());
        settings.setReminderEnabled(request.getReminderEnabled());
        settings.setReminderMinutesBefore(request.getReminderMinutesBefore());
        settings.setUpdatedAt(Instant.now());
        settings = settingsRepository.save(settings);
        return toResponse(settings);
    }

    private NotificationSettings getOrCreateSettings() {
        return settingsRepository.findById(SETTINGS_ID)
                .orElseGet(() -> settingsRepository.save(NotificationSettings.builder()
                        .id(SETTINGS_ID)
                        .confirmationEnabled(true)
                        .reminderEnabled(true)
                        .reminderMinutesBefore(30)
                        .updatedAt(Instant.now())
                        .build()));
    }

    private NotificationSettingsResponse toResponse(NotificationSettings settings) {
        return NotificationSettingsResponse.builder()
                .confirmationEnabled(settings.getConfirmationEnabled())
                .reminderEnabled(settings.getReminderEnabled())
                .reminderMinutesBefore(settings.getReminderMinutesBefore())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
