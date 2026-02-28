package com.americobarber.dto.response;

import com.americobarber.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Resposta de login ou cadastro: JWT e dados do usuário")
public class LoginResponse {

    @Schema(description = "Token JWT para Authorization: Bearer <token>")
    private String token;
    @Schema(description = "Tipo do token", example = "Bearer")
    private String type;
    @Schema(description = "ID do usuário")
    private Long userId;
    @Schema(description = "Nome do usuário")
    private String name;
    @Schema(description = "Email do usuário")
    private String email;
    @Schema(description = "Papel: ROLE_ADMIN ou ROLE_CLIENT")
    private UserRole role;
    @Schema(description = "Quando true, o admin atua como barbeiro (painel próprio)")
    private Boolean isBarber;
}
