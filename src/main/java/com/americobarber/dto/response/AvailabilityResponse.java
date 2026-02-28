package com.americobarber.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Slot de disponibilidade do barbeiro (dia da semana e horário)")
public class AvailabilityResponse {

    @Schema(description = "ID da disponibilidade")
    private Long id;
    @Schema(description = "ID do barbeiro")
    private Long barberId;
    @Schema(description = "Dia da semana: 1=Segunda a 7=Domingo")
    private Integer dayOfWeek;
    @Schema(description = "Horário de início")
    private LocalTime startTime;
    @Schema(description = "Horário de fim")
    private LocalTime endTime;
}
