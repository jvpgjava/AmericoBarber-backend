package com.americobarber.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Cancelamento com observação opcional")
public class CancelWithObservationRequest {

    @Schema(description = "Observação opcional (motivo do cancelamento)")
    private String observation;
}
