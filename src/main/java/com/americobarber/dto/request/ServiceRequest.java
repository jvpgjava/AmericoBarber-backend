package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Dados do serviço (criação ou atualização)")
public class ServiceRequest {

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255)
    @Schema(description = "Nome do serviço", example = "Corte masculino", requiredMode = Schema.RequiredMode.REQUIRED)
    private String name;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser positivo")
    @Schema(description = "Preço em reais", example = "45.00", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal price;

    @Schema(description = "Duração em minutos (opcional)", example = "30")
    private Integer durationMinutes;

    @Size(max = 1000)
    @Schema(description = "Descrição do serviço")
    private String description;

    @Schema(description = "Serviço ativo (default true na criação)")
    private Boolean active;

    @NotNull(message = "Barbeiro é obrigatório")
    @Schema(description = "ID do barbeiro responsável", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long barberId;
}
