package com.americobarber.service;

import com.americobarber.dto.request.NotificationSettingsRequest;
import com.americobarber.dto.response.NotificationSettingsResponse;

public interface NotificationSettingsService {

    NotificationSettingsResponse getSettings();

    NotificationSettingsResponse updateSettings(NotificationSettingsRequest request);
}
