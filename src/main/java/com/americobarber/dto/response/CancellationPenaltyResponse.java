package com.americobarber.dto.response;

import com.americobarber.enums.CancellationPenaltyStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Penalidade de cancelamento tardio ou no-show")
public class CancellationPenaltyResponse {

    @Schema(description = "ID da penalidade")
    private Long id;

    @Schema(description = "ID do cliente")
    private Long clientId;

    @Schema(description = "Nome do cliente")
    private String clientName;

    @Schema(description = "Telefone do cliente")
    private String clientPhone;

    @Schema(description = "ID do agendamento original")
    private Long appointmentId;

    @Schema(description = "Nome do barbeiro do agendamento")
    private String barberName;

    @Schema(description = "Nomes dos serviços do agendamento")
    private String serviceNames;

    @Schema(description = "Data do agendamento original")
    private LocalDate appointmentDate;

    @Schema(description = "Horário do agendamento original")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime appointmentTime;

    @Schema(description = "Valor da multa")
    private BigDecimal amount;

    @Schema(description = "Status da penalidade")
    private CancellationPenaltyStatus status;

    @Schema(description = "Comprovante de pagamento em Base64")
    private String proofImageData;

    @Schema(description = "Data de criação da penalidade")
    private Instant createdAt;

    @Schema(description = "Data da revisão pelo admin")
    private Instant reviewedAt;

    @Schema(description = "Nome do admin que revisou")
    private String reviewedByName;
}
