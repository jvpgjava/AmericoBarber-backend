package com.americobarber.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Um slot de disponibilidade do barbeiro")
public class AvailabilityRequest {

    @NotNull(message = "Dia da semana é obrigatório")
    @Min(1)
    @Max(7)
    @Schema(description = "Dia da semana: 1=Segunda, 2=Terça, ..., 7=Domingo", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Integer dayOfWeek;

    @NotNull(message = "Horário de início é obrigatório")
    @Schema(description = "Horário de início (HH:mm)", example = "09:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @NotNull(message = "Horário de fim é obrigatório")
    @Schema(description = "Horário de fim (HH:mm), deve ser após startTime", example = "18:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "Lista de intervalos/pausas do barbeiro")
    private List<com.americobarber.dto.BreakIntervalDto> breaks;
}
