package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Configuracao de notificacoes via WhatsApp")
public class NotificationSettingsRequest {

    @NotNull
    @Schema(description = "Envia confirmação automática por WhatsApp ao criar um agendamento")
    private Boolean confirmationEnabled;

    @NotNull
    @Schema(description = "Envia lembrete automático por WhatsApp antes do agendamento")
    private Boolean reminderEnabled;

    @NotNull
    @Min(value = 5, message = "Antecedência mínima é de 5 minutos")
    @Max(value = 1440, message = "Antecedência máxima é de 1440 minutos (24h)")
    @Schema(description = "Minutos de antecedência para o envio do lembrete")
    private Integer reminderMinutesBefore;
}
