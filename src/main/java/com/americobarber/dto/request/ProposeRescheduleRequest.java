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
@Schema(description = "Proposta de reagendamento pelo barbeiro (novo horário + mensagem opcional)")
public class ProposeRescheduleRequest {

    @NotNull
    @Schema(description = "Nova data proposta", required = true)
    private LocalDate proposedDate;

    @NotNull
    @Schema(description = "Nova hora de início proposta", required = true)
    private LocalTime proposedStartTime;

    @Schema(description = "Mensagem do barbeiro (ex.: imprevisto)")
    private String barberMessage;
}
