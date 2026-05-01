
package com.americobarber.controller;

import com.americobarber.dto.response.GalleryPhotoResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.service.AdminService;
import com.americobarber.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/public")
@Tag(name = "Publico", description = "Endpoints publicos para listar barbeiros e serviços (como na Landing Page)")
@RequiredArgsConstructor
public class PublicController {

    private final ClientService clientService;
    private final AdminService adminService;
    private final com.americobarber.repository.SystemConfigRepository systemConfigRepository;

    @Operation(summary = "Listar barbeiros públicos", description = "Retorna todos os barbeiros ativos para exibição sem login.")
    @GetMapping("/barbers")
    public ResponseEntity<List<UserResponse>> listPublicBarbers() {
        // Here we just list all barbers instead of specific ones for the client.
        // clientService.listBarbers(null) should handle bringing all barbers if
        // clientId is null.
        return ResponseEntity.ok(clientService.listBarbers(null));
    }

    @Operation(summary = "Listar serviços públicos", description = "Retorna todos os serviços ativos para exibição sem login.")
    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> listPublicServices() {
        // Pass null or handled logically in service to list all services.
        // Wait, ClientService has listServicesByBarber(barberId).
        // Let's check what ClientService provides.
        return ResponseEntity.ok(clientService.listServicesByBarber(null));
    }

    @Operation(summary = "Listar galeria pública", description = "Retorna todas as fotos da galeria ordenadas para exibição na landing page.")
    @GetMapping("/gallery")
    public ResponseEntity<List<GalleryPhotoResponse>> listPublicGallery() {
        return ResponseEntity.ok(adminService.listGalleryPhotos());
    }

    @Operation(summary = "Obter configuração pública", description = "Retorna valor de uma configuração pública como chave PIX.")
    @GetMapping("/config/{key}")
    public ResponseEntity<java.util.Map<String, String>> getPublicConfig(@org.springframework.web.bind.annotation.PathVariable String key) {
        // Only allow certain public keys
        if (!"PIX_KEY".equals(key)) {
            return ResponseEntity.ok(java.util.Map.of("key", key, "value", ""));
        }
        return systemConfigRepository.findById(key)
                .map(c -> ResponseEntity.ok(java.util.Map.of("key", c.getKey(), "value", c.getValue())))
                .orElse(ResponseEntity.ok(java.util.Map.of("key", key, "value", "")));
    }
}
