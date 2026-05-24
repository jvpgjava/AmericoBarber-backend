package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Revisao de comprovante pelo admin")
public class ReviewPenaltyRequest {

    @Schema(description = "Observacao interna do admin")
    private String adminNotes;
}
