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
@Schema(description = "Dados para criar/atualizar foto da galeria")
public class GalleryPhotoRequest {

    @NotBlank(message = "Imagem é obrigatória")
    @Schema(description = "Imagem em Base64")
    private String imageData;

    @Schema(description = "Título opcional da foto")
    private String title;

    @Schema(description = "Ordem de exibição (menor = primeiro)")
    private Integer displayOrder;
}
