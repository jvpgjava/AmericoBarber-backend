package com.americobarber.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Configuracao de notificacoes via WhatsApp")
public class NotificationSettingsResponse {

    private Boolean confirmationEnabled;
    private Boolean reminderEnabled;
    private Integer reminderMinutesBefore;
    private Instant updatedAt;
}
