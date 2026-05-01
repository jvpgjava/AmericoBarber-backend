package com.americobarber.controller;

import com.americobarber.dto.request.CreateBarberRequest;
import com.americobarber.dto.request.GalleryPhotoRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.request.UserUpdateRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.GalleryPhotoResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.service.AdminService;
import com.americobarber.service.CancellationPenaltyService;
import com.americobarber.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Gestão por administrador. Requer ROLE_ADMIN.")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final com.americobarber.service.BarberService barberService;
    private final CancellationPenaltyService cancellationPenaltyService;
    private final JwtUtil jwtUtil;
    private final com.americobarber.repository.SystemConfigRepository systemConfigRepository;

    // ================= AVAILABILITY =================

    @Operation(summary = "Obter disponibilidade de um barbeiro")
    @GetMapping("/barbers/{barberId}/availability")
    public ResponseEntity<List<com.americobarber.dto.response.AvailabilityResponse>> getBarberAvailability(
            @PathVariable Long barberId) {
        return ResponseEntity.ok(barberService.getAvailability(barberId));
    }

    @Operation(summary = "Definir disponibilidade de um barbeiro")
    @PutMapping("/barbers/{barberId}/availability")
    public ResponseEntity<List<com.americobarber.dto.response.AvailabilityResponse>> setBarberAvailability(
            @PathVariable Long barberId,
            @Valid @RequestBody List<com.americobarber.dto.request.AvailabilityRequest> body) {
        return ResponseEntity.ok(barberService.setAvailability(barberId, body));
    }

    @Operation(summary = "Obter dias de folga de um barbeiro")
    @GetMapping("/barbers/{barberId}/date-off")
    public ResponseEntity<List<java.time.LocalDate>> getBarberDateOff(@PathVariable Long barberId) {
        return ResponseEntity.ok(barberService.getDateOff(barberId));
    }

    @Operation(summary = "Definir dias de folga de um barbeiro")
    @PutMapping("/barbers/{barberId}/date-off")
    public ResponseEntity<List<java.time.LocalDate>> setBarberDateOff(
            @PathVariable Long barberId,
            @Valid @RequestBody com.americobarber.dto.request.BarberDateOffRequest body) {
        return ResponseEntity.ok(barberService.setDateOff(barberId, body));
    }

    @Operation(summary = "Atualizar intervalo da grade de um barbeiro")
    @PutMapping("/barbers/{barberId}/slot-interval")
    public ResponseEntity<UserResponse> updateBarberSlotInterval(
            @PathVariable Long barberId,
            @Valid @RequestBody com.americobarber.dto.request.SlotIntervalRequest body) {
        return ResponseEntity.ok(barberService.updateSlotInterval(barberId, body));
    }

    // ================= BARBERS =================

    @Operation(summary = "Criar barbeiro")
    @PostMapping("/barbers")
    public ResponseEntity<UserResponse> createBarber(@Valid @RequestBody CreateBarberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createBarber(request));
    }

    @Operation(summary = "Atualizar usuário")
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @Operation(summary = "Listar barbeiros")
    @GetMapping("/barbers")
    public ResponseEntity<List<UserResponse>> listBarbers() {
        return ResponseEntity.ok(adminService.listBarbers());
    }

    // ================= APPOINTMENTS =================

    @Operation(summary = "Criar agendamento para cliente")
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> createAppointmentForClient(
            @Valid @RequestBody com.americobarber.dto.request.AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAppointmentForClient(request));
    }

    @Operation(summary = "Listar clientes")
    @GetMapping("/clients")
    public ResponseEntity<List<UserResponse>> listClients() {
        return ResponseEntity.ok(adminService.listClients());
    }

    // ================= SERVICES =================

    @Operation(summary = "Criar serviço")
    @PostMapping("/services")
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createService(request));
    }

    @Operation(summary = "Atualizar serviço")
    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceResponse> updateService(@PathVariable Long id, @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(adminService.updateService(id, request));
    }

    @Operation(summary = "Listar todos os serviços")
    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> listAllServices() {
        return ResponseEntity.ok(adminService.listAllServices());
    }

    @Operation(summary = "Listar todos os agendamentos")
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> listAllAppointments() {
        return ResponseEntity.ok(adminService.listAllAppointments());
    }

    // ================= GALLERY =================

    @Operation(summary = "Listar fotos da galeria")
    @GetMapping("/gallery")
    public ResponseEntity<List<GalleryPhotoResponse>> listGalleryPhotos() {
        return ResponseEntity.ok(adminService.listGalleryPhotos());
    }

    @Operation(summary = "Adicionar foto à galeria")
    @PostMapping("/gallery")
    public ResponseEntity<GalleryPhotoResponse> addGalleryPhoto(@Valid @RequestBody GalleryPhotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addGalleryPhoto(request));
    }

    @Operation(summary = "Atualizar foto da galeria")
    @PutMapping("/gallery/{id}")
    public ResponseEntity<GalleryPhotoResponse> updateGalleryPhoto(@PathVariable Long id, @Valid @RequestBody GalleryPhotoRequest request) {
        return ResponseEntity.ok(adminService.updateGalleryPhoto(id, request));
    }

    @Operation(summary = "Excluir foto da galeria")
    @DeleteMapping("/gallery/{id}")
    public ResponseEntity<Void> deleteGalleryPhoto(@PathVariable Long id) {
        adminService.deleteGalleryPhoto(id);
        return ResponseEntity.noContent().build();
    }

    // ================= CANCELLATION PENALTIES =================

    @Operation(summary = "Listar penalidades de cancelamento", description = "Retorna todas as penalidades. Filtro opcional por status.")
    @GetMapping("/penalties")
    public ResponseEntity<List<com.americobarber.dto.response.CancellationPenaltyResponse>> listPenalties(
            @RequestParam(required = false) com.americobarber.enums.CancellationPenaltyStatus status) {
        if (status != null) {
            return ResponseEntity.ok(cancellationPenaltyService.listByStatus(status));
        }
        return ResponseEntity.ok(cancellationPenaltyService.listAll());
    }

    @Operation(summary = "Confirmar pagamento de penalidade")
    @PutMapping("/penalties/{id}/confirm")
    public ResponseEntity<com.americobarber.dto.response.CancellationPenaltyResponse> confirmPenalty(
            HttpServletRequest request, @PathVariable Long id) {
        Long adminId = getAdminId(request);
        return ResponseEntity.ok(cancellationPenaltyService.confirmPenalty(adminId, id));
    }

    @Operation(summary = "Rejeitar pagamento de penalidade")
    @PutMapping("/penalties/{id}/reject")
    public ResponseEntity<com.americobarber.dto.response.CancellationPenaltyResponse> rejectPenalty(
            HttpServletRequest request, @PathVariable Long id) {
        Long adminId = getAdminId(request);
        return ResponseEntity.ok(cancellationPenaltyService.rejectPenalty(adminId, id));
    }

    @Operation(summary = "Bloquear cliente")
    @PutMapping("/clients/{clientId}/block")
    public ResponseEntity<Void> blockClient(@PathVariable Long clientId) {
        cancellationPenaltyService.blockClient(clientId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Desbloquear cliente")
    @PutMapping("/clients/{clientId}/unblock")
    public ResponseEntity<Void> unblockClient(@PathVariable Long clientId) {
        cancellationPenaltyService.unblockClient(clientId);
        return ResponseEntity.noContent().build();
    }

    // ================= SYSTEM CONFIG =================

    @Operation(summary = "Obter configuração do sistema", description = "Retorna o valor de uma configuração pelo nome da chave (ex: PIX_KEY).")
    @GetMapping("/config/{key}")
    public ResponseEntity<java.util.Map<String, String>> getConfig(@PathVariable String key) {
        return systemConfigRepository.findById(key)
                .map(c -> ResponseEntity.ok(java.util.Map.of("key", c.getKey(), "value", c.getValue())))
                .orElse(ResponseEntity.ok(java.util.Map.of("key", key, "value", "")));
    }

    @Operation(summary = "Salvar configuração do sistema", description = "Cria ou atualiza uma configuração do sistema (ex: PIX_KEY).")
    @PutMapping("/config/{key}")
    public ResponseEntity<java.util.Map<String, String>> setConfig(
            @PathVariable String key,
            @RequestBody java.util.Map<String, String> body) {
        String value = body.getOrDefault("value", "");
        com.americobarber.entity.SystemConfig config = systemConfigRepository.findById(key)
                .orElse(com.americobarber.entity.SystemConfig.builder().key(key).build());
        config.setValue(value);
        systemConfigRepository.save(config);
        return ResponseEntity.ok(java.util.Map.of("key", key, "value", value));
    }

    private Long getAdminId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        return jwtUtil.getUserIdFromToken(token);
    }
}
