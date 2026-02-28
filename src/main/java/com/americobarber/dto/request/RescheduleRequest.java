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
@Schema(description = "Reagendamento pelo cliente (novo horário + observação opcional)")
public class RescheduleRequest {

    @NotNull
    @Schema(description = "Nova data", required = true)
    private LocalDate newDate;

    @NotNull
    @Schema(description = "Nova hora de início", required = true)
    private LocalTime newStartTime;

    @Schema(description = "Observação opcional (motivo do reagendamento)")
    private String observation;
}
