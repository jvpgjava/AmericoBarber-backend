package com.americobarber.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Um intervalo (pausa) de disponibilidade do barbeiro")
public class BreakIntervalDto {

    @NotNull(message = "Horário de início do intervalo é obrigatório")
    @Schema(description = "Horário de início do intervalo (HH:mm)", example = "12:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "Horário de fim do intervalo é obrigatório")
    @Schema(description = "Horário de fim do intervalo (HH:mm), deve ser após startTime", example = "13:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;
}
