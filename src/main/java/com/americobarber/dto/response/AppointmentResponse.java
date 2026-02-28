package com.americobarber.dto.response;

import com.americobarber.enums.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Agendamento (cliente, barbeiro, serviço, data/hora, status)")
public class AppointmentResponse {

    @Schema(description = "ID do agendamento")
    private Long id;
    @Schema(description = "ID do cliente")
    private Long clientId;
    @Schema(description = "Nome do cliente")
    private String clientName;
    @Schema(description = "ID do barbeiro")
    private Long barberId;
    @Schema(description = "Nome do barbeiro")
    private String barberName;
    @Schema(description = "ID do serviço")
    private Long serviceId;
    @Schema(description = "Nome do serviço")
    private String serviceName;
    @Schema(description = "Data do agendamento")
    private LocalDate date;
    @Schema(description = "Horário de início")
    private LocalTime startTime;
    @Schema(description = "Horário de fim (calculado pela duração do serviço)")
    private LocalTime endTime;
    @Schema(description = "Status: AGENDADO, CANCELADO, CANCELADO_POR_BARBEIRO, CANCELADO_POR_CLIENTE, PROPOSTA_REAGENDAMENTO, FINALIZADO")
    private AppointmentStatus status;
    @Schema(description = "Observação opcional do cliente (cancelamento/reagendamento)")
    private String observation;
    @Schema(description = "Mensagem do barbeiro ao propor reagendamento (ex.: imprevisto)")
    private String barberMessage;
    @Schema(description = "Data proposta pelo barbeiro para reagendamento")
    private LocalDate proposedDate;
    @Schema(description = "Hora início proposta pelo barbeiro")
    private LocalTime proposedStartTime;
    @Schema(description = "Hora fim proposta pelo barbeiro")
    private LocalTime proposedEndTime;
    @Schema(description = "Data de criação do registro")
    private Instant createdAt;
}
