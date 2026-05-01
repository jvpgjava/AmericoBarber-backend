package com.americobarber.controller;

import com.americobarber.dto.request.AppointmentRequest;
import com.americobarber.dto.request.CancelWithObservationRequest;
import com.americobarber.dto.request.RescheduleRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.service.CancellationPenaltyService;
import com.americobarber.service.ClientService;
import com.americobarber.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
@PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
@Tag(name = "Cliente", description = "Perfil, agendamentos e histórico do cliente. Requer ROLE_CLIENT (ou ROLE_ADMIN). Usuário identificado pelo JWT.")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final JwtUtil jwtUtil;
    private final CancellationPenaltyService cancellationPenaltyService;

    @Operation(summary = "Meu perfil", description = "Retorna dados do cliente autentado (ID extraído do JWT).")
    @ApiResponse(responseCode = "200", description = "Dados do usuário")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.getProfile(clientId));
    }

    @Operation(summary = "Atualizar perfil", description = "Atualiza dados do cliente autenticado (nome, email, telefone, foto de perfil).")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado")
    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            HttpServletRequest request,
            @Valid @RequestBody com.americobarber.dto.request.UserUpdateRequest body) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.updateProfile(clientId, body));
    }

    @Operation(summary = "Meus agendamentos", description = "Lista agendamentos ativos (status AGENDADO) do cliente.")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos")
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> myAppointments(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.myAppointments(clientId));
    }

    @Operation(summary = "Histórico", description = "Lista todos os agendamentos do cliente (inclui cancelados e finalizados).")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos")
    @GetMapping("/history")
    public ResponseEntity<List<AppointmentResponse>> myHistory(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.myHistory(clientId));
    }

    @Operation(summary = "Criar agendamento", description = "Cria novo agendamento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Agendamento criado"),
        @ApiResponse(responseCode = "422", description = "Regra de negócio", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Não encontrado", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> createAppointment(
            HttpServletRequest request,
            @Valid @RequestBody AppointmentRequest body) {
        Long clientId = getUserId(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createAppointment(clientId, body));
    }

    @Operation(summary = "Cancelar agendamento", description = "Altera status para CANCELADO_POR_CLIENTE. Se faltam <12h, gera multa.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cancelado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Agendamento não é do cliente ou já cancelado", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> cancelAppointment(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @RequestBody(required = false) CancelWithObservationRequest body) {
        Long clientId = getUserId(request);
        String observation = body != null ? body.getObservation() : null;
        clientService.cancelAppointment(clientId, id, observation);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Aceitar proposta de reagendamento")
    @PutMapping("/appointments/{id}/accept-proposal")
    public ResponseEntity<AppointmentResponse> acceptProposal(
            HttpServletRequest request,
            @PathVariable Long id) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.acceptProposal(clientId, id));
    }

    @Operation(summary = "Rejeitar proposta de reagendamento")
    @PutMapping("/appointments/{id}/reject-proposal")
    public ResponseEntity<Void> rejectProposal(
            HttpServletRequest request,
            @PathVariable Long id) {
        Long clientId = getUserId(request);
        clientService.rejectProposal(clientId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reagendar")
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> reschedule(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest body) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.reschedule(clientId, id, body));
    }

    @Operation(summary = "Listar barbeiros")
    @GetMapping("/barbers")
    public ResponseEntity<List<UserResponse>> listBarbers(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.listBarbers(clientId));
    }

    @Operation(summary = "Serviços do barbeiro")
    @GetMapping("/barbers/{barberId}/services")
    public ResponseEntity<List<ServiceResponse>> listServicesByBarber(@PathVariable Long barberId) {
        return ResponseEntity.ok(clientService.listServicesByBarber(barberId));
    }

    @Operation(summary = "Disponibilidade semanal do barbeiro")
    @GetMapping("/barbers/{barberId}/availability")
    public ResponseEntity<List<com.americobarber.dto.response.AvailabilityResponse>> getBarberAvailability(
            @PathVariable Long barberId) {
        return ResponseEntity.ok(clientService.getBarberAvailability(barberId));
    }

    @Operation(summary = "Dias de folga do barbeiro")
    @GetMapping("/barbers/{barberId}/date-off")
    public ResponseEntity<List<LocalDate>> getBarberDateOff(
            HttpServletRequest request,
            @PathVariable Long barberId) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.getBarberDateOff(clientId, barberId));
    }

    @Operation(summary = "Horários disponíveis")
    @GetMapping("/barbers/{barberId}/available-times")
    public ResponseEntity<List<java.time.LocalTime>> getAvailableTimes(
            @PathVariable Long barberId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<Long> serviceIds) {
        return ResponseEntity.ok(clientService.getAvailableTimes(barberId, date, serviceIds));
    }

    // ================= CANCELLATION PENALTIES =================

    @Operation(summary = "Penalidade pendente", description = "Retorna a penalidade de cancelamento ativa do cliente (PENDING ou AWAITING_REVIEW), se existir.")
    @GetMapping("/penalties/pending")
    public ResponseEntity<com.americobarber.dto.response.CancellationPenaltyResponse> getPendingPenalty(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(cancellationPenaltyService.getActivePenalty(clientId));
    }

    @Operation(summary = "Enviar comprovante", description = "Envia comprovante de pagamento (base64) para uma penalidade pendente.")
    @PostMapping("/penalties/{id}/proof")
    public ResponseEntity<com.americobarber.dto.response.CancellationPenaltyResponse> submitProof(
            HttpServletRequest request,
            @PathVariable Long id,
            @Valid @RequestBody com.americobarber.dto.request.PenaltyProofRequest body) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(cancellationPenaltyService.submitProof(clientId, id, body.getProofImageData()));
    }

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        return jwtUtil.getUserIdFromToken(token);
    }
}
