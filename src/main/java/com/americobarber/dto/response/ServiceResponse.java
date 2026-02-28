package com.americobarber.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Serviço oferecido por um barbeiro")
public class ServiceResponse {

    @Schema(description = "ID do serviço")
    private Long id;
    @Schema(description = "Nome do serviço")
    private String name;
    @Schema(description = "Preço em reais")
    private BigDecimal price;
    @Schema(description = "Duração em minutos (null se não definido)")
    private Integer durationMinutes;
    @Schema(description = "Descrição")
    private String description;
    @Schema(description = "Serviço ativo")
    private Boolean active;
    @Schema(description = "ID do barbeiro")
    private Long barberId;
    @Schema(description = "Nome do barbeiro")
    private String barberName;
}
