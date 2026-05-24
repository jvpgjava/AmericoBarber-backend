package com.americobarber.dto.response;

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
@Schema(description = "Configuracoes de pagamento PIX")
public class PaymentSettingsResponse {

    private String pixKey;
    private Instant updatedAt;
}
