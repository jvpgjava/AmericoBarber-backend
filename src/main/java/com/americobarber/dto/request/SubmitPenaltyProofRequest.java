package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Envio de comprovante de pagamento da penalidade")
public class SubmitPenaltyProofRequest {

    @NotBlank
    @Schema(description = "Imagem do comprovante em base64")
    private String proofImage;
}
