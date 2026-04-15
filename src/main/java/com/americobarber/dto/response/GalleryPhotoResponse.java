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
@Schema(description = "Foto da galeria de cortes")
public class GalleryPhotoResponse {

    @Schema(description = "ID da foto")
    private Long id;

    @Schema(description = "Imagem em Base64")
    private String imageData;

    @Schema(description = "Título da foto")
    private String title;

    @Schema(description = "Ordem de exibição")
    private Integer displayOrder;

    @Schema(description = "Data de criação")
    private Instant createdAt;
}
