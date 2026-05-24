package com.americobarber.dto.response;

import com.americobarber.enums.PenaltyStatus;
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
@Schema(description = "Penalidade por cancelamento tardio")
public class CancellationPenaltyResponse {

    private Long id;
    private Long clientId;
    private String clientName;
    private String clientEmail;
    private String clientPhone;
    private Long appointmentId;
    private LocalDate appointmentDate;
    private LocalTime appointmentStartTime;
    private BigDecimal amount;
    private PenaltyStatus status;
    private String proofImage;
    private String adminNotes;
    private Long reviewedById;
    private String reviewedByName;
    private Instant createdAt;
    private Instant proofSubmittedAt;
    private Instant reviewedAt;
}
