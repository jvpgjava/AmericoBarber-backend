package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Campos opcionais para atualização de usuário")
public class UserUpdateRequest {

    @Size(max = 255)
    @Schema(description = "Nome")
    private String name;

    @Email
    @Size(max = 255)
    @Schema(description = "Email")
    private String email;

    @Size(max = 20)
    @Schema(description = "Telefone")
    private String phone;

    @Size(min = 6, max = 255)
    @Schema(description = "Nova senha (mínimo 6 caracteres)")
    private String password;

    @Schema(description = "Usuário ativo ou inativo")
    private Boolean active;

    @Schema(description = "ID do barbeiro ao qual o cliente está vinculado (apenas para role CLIENT)")
    private Long assignedBarberId;
}
