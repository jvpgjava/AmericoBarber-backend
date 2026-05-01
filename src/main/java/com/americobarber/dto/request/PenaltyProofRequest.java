package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Envio de comprovante de pagamento de penalidade")
public class PenaltyProofRequest {

    @NotBlank(message = "Comprovante é obrigatório")
    @Schema(description = "Imagem do comprovante de pagamento em Base64")
    private String proofImageData;
}
