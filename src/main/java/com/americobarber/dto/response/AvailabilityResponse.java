package com.americobarber.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;
    @Schema(description = "Horário de fim")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    @Schema(description = "Início do intervalo/pausa")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakStartTime;

    @Schema(description = "Fim do intervalo/pausa")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime breakEndTime;
}
