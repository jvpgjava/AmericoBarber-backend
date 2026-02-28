package com.americobarber.dto.request;

import com.americobarber.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para cadastro de usuário")
public class RegisterRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    @Schema(description = "Nome completo", example = "João Silva", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email
    @Size(max = 255)
    @Schema(description = "Email único", example = "joao@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 14)
    @Schema(description = "CPF único (apenas números ou formatado)", example = "12345678900", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(description = "Telefone único", example = "11999998888", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 255)
    @Schema(description = "Senha (mínimo 6 caracteres)", example = "senha123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;

    @NotNull(message = "Role é obrigatória")
    @Schema(description = "Papel: use ROLE_CLIENT para cadastro público. Barbeiros são criados pelo admin.", requiredMode = Schema.RequiredMode.REQUIRED)
    private UserRole role;
}
