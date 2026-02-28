package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para criar agendamento")
public class AppointmentRequest {

    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente (deve ser o mesmo do JWT)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long clientId;

    @NotNull(message = "Barbeiro é obrigatório")
    @Schema(description = "ID do barbeiro", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long barberId;

    @NotNull(message = "Serviço é obrigatório")
    @Schema(description = "ID do serviço (deve ser do barbeiro informado)", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long serviceId;

    @NotNull(message = "Data é obrigatória")
    @Schema(description = "Data do agendamento (yyyy-MM-dd)", example = "2025-03-01", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate date;

    @NotNull(message = "Horário de início é obrigatório")
    @Schema(description = "Horário de início (HH:mm). Fim calculado pela duração do serviço", example = "14:00", requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalTime startTime;
}
