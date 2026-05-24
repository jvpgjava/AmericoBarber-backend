package com.americobarber.controller;

import com.americobarber.dto.request.CreateBarberRequest;
import com.americobarber.dto.request.GalleryPhotoRequest;
import com.americobarber.dto.request.ReviewPenaltyRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.request.UpdatePixKeyRequest;
import com.americobarber.dto.request.UserUpdateRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.CancellationPenaltyResponse;
import com.americobarber.dto.response.GalleryPhotoResponse;
import com.americobarber.dto.response.PaymentSettingsResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.service.AdminService;
import com.americobarber.service.CancellationPenaltyService;
import com.americobarber.service.PaymentSettingsService;
import com.americobarber.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Gestão por administrador. Requer ROLE_ADMIN (Bearer JWT). Barbeiros são admins com isBarber=true criados via POST /barbers.")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final com.americobarber.service.BarberService barberService;
    private final CancellationPenaltyService cancellationPenaltyService;
    private final PaymentSettingsService paymentSettingsService;
    private final JwtUtil jwtUtil;

    @Operation(summary = "Obter disponibilidade de um barbeiro", description = "Retorna os horários de atendimento configurados por dia da semana para um barbeiro específico.")
    @ApiResponse(responseCode = "200", description = "Lista de disponibilidades")
    @GetMapping("/barbers/{barberId}/availability")
    public ResponseEntity<List<com.americobarber.dto.response.AvailabilityResponse>> getBarberAvailability(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId) {
        return ResponseEntity.ok(barberService.getAvailability(barberId));
    }

    @Operation(summary = "Definir disponibilidade de um barbeiro", description = "Substitui toda a disponibilidade de um barbeiro específico.")
    @ApiResponse(responseCode = "200", description = "Disponibilidade atualizada")
    @PutMapping("/barbers/{barberId}/availability")
    public ResponseEntity<List<com.americobarber.dto.response.AvailabilityResponse>> setBarberAvailability(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId,
            @Valid @RequestBody List<com.americobarber.dto.request.AvailabilityRequest> body) {
        return ResponseEntity.ok(barberService.setAvailability(barberId, body));
    }

    @Operation(summary = "Obter dias de folga de um barbeiro", description = "Retorna as datas em que o barbeiro específico não atende.")
    @ApiResponse(responseCode = "200", description = "Lista de datas")
    @GetMapping("/barbers/{barberId}/date-off")
    public ResponseEntity<List<java.time.LocalDate>> getBarberDateOff(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId) {
        return ResponseEntity.ok(barberService.getDateOff(barberId));
    }

    @Operation(summary = "Definir dias de folga de um barbeiro", description = "Substitui a lista de datas em que o barbeiro específico não atende.")
    @ApiResponse(responseCode = "200", description = "Lista atualizada de datas")
    @PutMapping("/barbers/{barberId}/date-off")
    public ResponseEntity<List<java.time.LocalDate>> setBarberDateOff(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId,
            @Valid @RequestBody com.americobarber.dto.request.BarberDateOffRequest body) {
        return ResponseEntity.ok(barberService.setDateOff(barberId, body));
    }

    @Operation(summary = "Atualizar intervalo da grade de um barbeiro", description = "Define o intervalo em minutos para a grade de horários de um barbeiro específico.")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado")
    @PutMapping("/barbers/{barberId}/slot-interval")
    public ResponseEntity<UserResponse> updateBarberSlotInterval(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId,
            @Valid @RequestBody com.americobarber.dto.request.SlotIntervalRequest body) {
        return ResponseEntity.ok(barberService.updateSlotInterval(barberId, body));
    }

    @Operation(summary = "Criar barbeiro", description = "Cadastra novo admin barbeiro (role ADMIN + isBarber). Cada um vê só seus clientes/serviços/agendamentos.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Barbeiro criado"),
        @ApiResponse(responseCode = "422", description = "Email/CPF/telefone já existente", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PostMapping("/barbers")
    public ResponseEntity<UserResponse> createBarber(@Valid @RequestBody CreateBarberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createBarber(request));
    }

    @Operation(summary = "Atualizar usuário", description = "Atualiza nome, email, telefone, senha ou ativo por ID.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuário atualizado"),
        @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PutMapping("/users/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @Parameter(description = "ID do usuário") @PathVariable Long id,
            @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(adminService.updateUser(id, request));
    }

    @Operation(summary = "Listar barbeiros", description = "Retorna admins que são barbeiros (isBarber=true).")
    @ApiResponse(responseCode = "200", description = "Lista de barbeiros")
    @GetMapping("/barbers")
    public ResponseEntity<List<UserResponse>> listBarbers() {
        return ResponseEntity.ok(adminService.listBarbers());
    }

    @Operation(summary = "Criar agendamento para cliente", description = "Cria agendamento em nome de um cliente. Admin não precisa ser o cliente.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Agendamento criado"),
        @ApiResponse(responseCode = "422", description = "Regra de negócio (horário ocupado, serviço inativo, etc.)", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Cliente, barbeiro ou serviço não encontrado", content = @Content())
    })
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> createAppointmentForClient(
            @Valid @RequestBody com.americobarber.dto.request.AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createAppointmentForClient(request));
    }

    @Operation(summary = "Listar clientes", description = "Retorna todos os usuários com role ROLE_CLIENT.")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    @GetMapping("/clients")
    public ResponseEntity<List<UserResponse>> listClients() {
        return ResponseEntity.ok(adminService.listClients());
    }

    @Operation(summary = "Criar serviço", description = "Cadastra serviço vinculado a um barbeiro. Apenas admin.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Serviço criado"),
        @ApiResponse(responseCode = "404", description = "Barbeiro não encontrado", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PostMapping("/services")
    public ResponseEntity<ServiceResponse> createService(@Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createService(request));
    }

    @Operation(summary = "Atualizar serviço", description = "Atualiza nome, preço, duração, descrição, ativo ou barbeiro do serviço.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço atualizado"),
        @ApiResponse(responseCode = "404", description = "Serviço ou barbeiro não encontrado", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PutMapping("/services/{id}")
    public ResponseEntity<ServiceResponse> updateService(
            @Parameter(description = "ID do serviço") @PathVariable Long id,
            @Valid @RequestBody ServiceRequest request) {
        return ResponseEntity.ok(adminService.updateService(id, request));
    }

    @Operation(summary = "Listar todos os serviços", description = "Retorna todos os serviços, de todos os barbeiros.")
    @ApiResponse(responseCode = "200", description = "Lista de serviços")
    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> listAllServices() {
        return ResponseEntity.ok(adminService.listAllServices());
    }

    @Operation(summary = "Listar todos os agendamentos", description = "Retorna todos os agendamentos do sistema.")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos")
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> listAllAppointments() {
        return ResponseEntity.ok(adminService.listAllAppointments());
    }


    @Operation(summary = "Listar fotos da galeria", description = "Retorna todas as fotos da galeria ordenadas por displayOrder.")
    @ApiResponse(responseCode = "200", description = "Lista de fotos")
    @GetMapping("/gallery")
    public ResponseEntity<List<GalleryPhotoResponse>> listGalleryPhotos() {
        return ResponseEntity.ok(adminService.listGalleryPhotos());
    }

    @Operation(summary = "Adicionar foto à galeria", description = "Adiciona nova foto (base64) à galeria de cortes.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Foto adicionada"),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PostMapping("/gallery")
    public ResponseEntity<GalleryPhotoResponse> addGalleryPhoto(@Valid @RequestBody GalleryPhotoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.addGalleryPhoto(request));
    }

    @Operation(summary = "Atualizar foto da galeria", description = "Atualiza imagem, título ou ordem de exibição.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Foto atualizada"),
        @ApiResponse(responseCode = "404", description = "Foto não encontrada", content = @Content())
    })
    @PutMapping("/gallery/{id}")
    public ResponseEntity<GalleryPhotoResponse> updateGalleryPhoto(
            @Parameter(description = "ID da foto") @PathVariable Long id,
            @Valid @RequestBody GalleryPhotoRequest request) {
        return ResponseEntity.ok(adminService.updateGalleryPhoto(id, request));
    }

    @Operation(summary = "Excluir foto da galeria", description = "Remove permanentemente uma foto da galeria.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Foto excluída"),
        @ApiResponse(responseCode = "404", description = "Foto não encontrada", content = @Content())
    })
    @DeleteMapping("/gallery/{id}")
    public ResponseEntity<Void> deleteGalleryPhoto(
            @Parameter(description = "ID da foto") @PathVariable Long id) {
        adminService.deleteGalleryPhoto(id);
        return ResponseEntity.noContent().build();
    }
    @Operation(summary = "Listar penalidades", description = "Lista comprovantes de cancelamento tardio enviados pelos clientes.")
    @GetMapping("/cancellation-penalties")
    public ResponseEntity<List<CancellationPenaltyResponse>> listCancellationPenalties() {
        return ResponseEntity.ok(cancellationPenaltyService.listAll());
    }

    @Operation(summary = "Aprovar comprovante", description = "Confirma que o pagamento foi recebido corretamente.")
    @PostMapping("/cancellation-penalties/{id}/approve")
    public ResponseEntity<CancellationPenaltyResponse> approvePenalty(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) ReviewPenaltyRequest body) {
        Long adminId = getUserId(request);
        return ResponseEntity.ok(cancellationPenaltyService.approve(id, adminId, body));
    }

    @Operation(summary = "Rejeitar comprovante", description = "Bloqueia novamente o cliente por comprovante inválido.")
    @PostMapping("/cancellation-penalties/{id}/reject")
    public ResponseEntity<CancellationPenaltyResponse> rejectPenalty(
            HttpServletRequest request,
            @PathVariable Long id,
            @RequestBody(required = false) ReviewPenaltyRequest body) {
        Long adminId = getUserId(request);
        return ResponseEntity.ok(cancellationPenaltyService.reject(id, adminId, body));
    }

    @Operation(summary = "Obter chave PIX", description = "Retorna a chave PIX configurada para pagamentos de penalidade.")
    @GetMapping("/payment-settings")
    public ResponseEntity<PaymentSettingsResponse> getPaymentSettings() {
        return ResponseEntity.ok(paymentSettingsService.getSettings());
    }

    @Operation(summary = "Atualizar chave PIX", description = "Atualiza a chave PIX exibida aos clientes bloqueados.")
    @PutMapping("/payment-settings")
    public ResponseEntity<PaymentSettingsResponse> updatePaymentSettings(
            @Valid @RequestBody UpdatePixKeyRequest body) {
        return ResponseEntity.ok(paymentSettingsService.updatePixKey(body));
    }

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        return jwtUtil.getUserIdFromToken(token);
    }
}
