package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados para cadastro de um admin barbeiro (cada um vê só seus clientes/serviços/agendamentos)")
public class CreateBarberRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    @Schema(description = "Nome completo", example = "Carlos Barbeiro", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotBlank(message = "Email é obrigatório")
    @Email
    @Size(max = 255)
    @Schema(description = "Email único", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "CPF é obrigatório")
    @Size(max = 14)
    @Schema(description = "CPF único", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cpf;

    @NotBlank(message = "Telefone é obrigatório")
    @Size(max = 20)
    @Schema(description = "Telefone único", requiredMode = Schema.RequiredMode.REQUIRED)
    private String phone;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 6, max = 255)
    @Schema(description = "Senha (mínimo 6 caracteres)", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
