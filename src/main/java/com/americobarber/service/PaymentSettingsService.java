package com.americobarber.service;

import com.americobarber.dto.request.UpdatePixKeyRequest;
import com.americobarber.dto.response.PaymentSettingsResponse;

public interface PaymentSettingsService {

    PaymentSettingsResponse getSettings();

    PaymentSettingsResponse updatePixKey(UpdatePixKeyRequest request);
}
