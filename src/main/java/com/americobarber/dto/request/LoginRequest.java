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
@Schema(description = "Credenciais de login")
public class LoginRequest {

    @NotBlank(message = "Email é obrigatório")
    @Schema(description = "Email do usuário", example = "cliente@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "Senha é obrigatória")
    @Schema(description = "Senha", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
