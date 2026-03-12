package com.americobarber.controller;

import com.americobarber.dto.request.AppointmentRequest;
import com.americobarber.dto.request.CancelWithObservationRequest;
import com.americobarber.dto.request.RescheduleRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
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

    @Operation(summary = "Meu perfil", description = "Retorna dados do cliente autentado (ID extraído do JWT).")
    @ApiResponse(responseCode = "200", description = "Dados do usuário")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.getProfile(clientId));
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

    @Operation(summary = "Criar agendamento", description = "Cria novo agendamento. Valida: serviço ativo, sem double booking, cliente não pode ter dois no mesmo horário. clientId no body deve ser o mesmo do JWT.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Agendamento criado"),
        @ApiResponse(responseCode = "422", description = "Regra de negócio (horário ocupado, serviço inativo, etc.)", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Cliente, barbeiro ou serviço não encontrado", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PostMapping("/appointments")
    public ResponseEntity<AppointmentResponse> createAppointment(
            HttpServletRequest request,
            @Valid @RequestBody AppointmentRequest body) {
        Long clientId = getUserId(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.createAppointment(clientId, body));
    }

    @Operation(summary = "Cancelar agendamento", description = "Altera status para CANCELADO_POR_CLIENTE. Observação opcional no body.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cancelado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Agendamento não é do cliente ou já está cancelado/finalizado", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
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

    @Operation(summary = "Aceitar proposta de reagendamento", description = "Aceita a proposta do barbeiro e atualiza data/hora do agendamento.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento atualizado"),
        @ApiResponse(responseCode = "422", description = "Sem proposta pendente", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @PutMapping("/appointments/{id}/accept-proposal")
    public ResponseEntity<AppointmentResponse> acceptProposal(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.acceptProposal(clientId, id));
    }

    @Operation(summary = "Rejeitar proposta de reagendamento", description = "Rejeita a proposta e cancela o agendamento (CANCELADO_POR_CLIENTE).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Proposta rejeitada"),
        @ApiResponse(responseCode = "422", description = "Sem proposta pendente", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @PutMapping("/appointments/{id}/reject-proposal")
    public ResponseEntity<Void> rejectProposal(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id) {
        Long clientId = getUserId(request);
        clientService.rejectProposal(clientId, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reagendar", description = "Altera data/hora do agendamento. Observação opcional.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Agendamento reagendado"),
        @ApiResponse(responseCode = "422", description = "Horário ocupado ou agendamento inválido", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @PutMapping("/appointments/{id}/reschedule")
    public ResponseEntity<AppointmentResponse> reschedule(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @Valid @RequestBody RescheduleRequest body) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.reschedule(clientId, id, body));
    }

    @Operation(summary = "Listar barbeiros", description = "Se o cliente tem barbeiro vinculado, retorna só ele; senão, todos os barbeiros ativos.")
    @ApiResponse(responseCode = "200", description = "Lista de barbeiros")
    @GetMapping("/barbers")
    public ResponseEntity<List<UserResponse>> listBarbers(HttpServletRequest request) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.listBarbers(clientId));
    }

    @Operation(summary = "Serviços do barbeiro", description = "Lista serviços ativos de um barbeiro (para montar o formulário de agendamento).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de serviços"),
        @ApiResponse(responseCode = "404", description = "Barbeiro não encontrado", content = @Content())
    })
    @GetMapping("/barbers/{barberId}/services")
    public ResponseEntity<List<ServiceResponse>> listServicesByBarber(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId) {
        return ResponseEntity.ok(clientService.listServicesByBarber(barberId));
    }

    @Operation(summary = "Disponibilidade semanal do barbeiro", description = "Retorna os dias e horários em que o barbeiro atende (para o calendário do cliente).")
    @GetMapping("/barbers/{barberId}/availability")
    public ResponseEntity<List<com.americobarber.dto.response.AvailabilityResponse>> getBarberAvailability(
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId) {
        return ResponseEntity.ok(clientService.getBarberAvailability(barberId));
    }

    @Operation(summary = "Dias de folga do barbeiro", description = "Datas em que o barbeiro não atende (para o calendário do cliente). Se cliente tem barbeiro vinculado, só pode consultar o seu.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de datas"),
        @ApiResponse(responseCode = "422", description = "Cliente vinculado a outro barbeiro", content = @Content())
    })
    @GetMapping("/barbers/{barberId}/date-off")
    public ResponseEntity<List<LocalDate>> getBarberDateOff(
            HttpServletRequest request,
            @Parameter(description = "ID do barbeiro") @PathVariable Long barberId) {
        Long clientId = getUserId(request);
        return ResponseEntity.ok(clientService.getBarberDateOff(clientId, barberId));
    }

    @Operation(summary = "Horários disponíveis", description = "Calcula horários disponíveis para um barbeiro em uma data específica considerando serviços escolhidos.")
    @GetMapping("/barbers/{barberId}/available-times")
    public ResponseEntity<List<java.time.LocalTime>> getAvailableTimes(
            @PathVariable Long barberId,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam List<Long> serviceIds) {
        return ResponseEntity.ok(clientService.getAvailableTimes(barberId, date, serviceIds));
    }

    private Long getUserId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        return jwtUtil.getUserIdFromToken(token);
    }
}
