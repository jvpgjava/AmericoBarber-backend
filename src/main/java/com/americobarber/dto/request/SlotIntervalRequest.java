package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SlotIntervalRequest {

    @NotNull(message = "O intervalo é obrigatório")
    @Min(value = 5, message = "O intervalo mínimo é de 5 minutos")
    @Max(value = 120, message = "O intervalo máximo é de 120 minutos")
    @Schema(description = "Intervalo em minutos para a grade de agendamentos", example = "30")
    private Integer slotIntervalMinutes;
}
