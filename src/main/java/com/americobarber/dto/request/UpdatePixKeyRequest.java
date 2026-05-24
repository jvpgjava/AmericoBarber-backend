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
@Schema(description = "Atualizacao da chave PIX")
public class UpdatePixKeyRequest {

    @NotBlank
    @Schema(description = "Chave PIX exibida aos clientes")
    private String pixKey;
}
