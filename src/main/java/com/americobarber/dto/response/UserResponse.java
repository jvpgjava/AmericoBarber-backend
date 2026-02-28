package com.americobarber.dto.response;

import com.americobarber.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados do usuário. Roles: ROLE_ADMIN (se isBarber=true é barbeiro) ou ROLE_CLIENT.")
public class UserResponse {

    @Schema(description = "ID do usuário")
    private Long id;
    @Schema(description = "Nome")
    private String name;
    @Schema(description = "Email")
    private String email;
    @Schema(description = "CPF")
    private String cpf;
    @Schema(description = "Telefone")
    private String phone;
    @Schema(description = "Papel: ROLE_ADMIN ou ROLE_CLIENT")
    private UserRole role;
    @Schema(description = "Usuário ativo")
    private Boolean active;
    @Schema(description = "Quando true, o admin atua como barbeiro (vê só seus clientes/serviços/agendamentos)")
    private Boolean isBarber;
    @Schema(description = "Data de criação")
    private Instant createdAt;
    @Schema(description = "ID do barbeiro ao qual o cliente está vinculado (apenas para role CLIENT)")
    private Long assignedBarberId;
}
