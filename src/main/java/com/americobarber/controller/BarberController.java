package com.americobarber.controller;

import com.americobarber.dto.request.AvailabilityRequest;
import com.americobarber.dto.request.BarberDateOffRequest;
import com.americobarber.dto.request.CancelWithObservationRequest;
import com.americobarber.dto.request.ProposeRescheduleRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.AvailabilityResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.entity.User;
import com.americobarber.exception.BusinessException;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.BarberService;
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
@RequestMapping("/api/barbers")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Barbeiro", description = "Painel do admin barbeiro. Apenas admins com isBarber=true. Cada um vê só seus clientes, agendamentos, serviços e disponibilidade.")
@RequiredArgsConstructor
public class BarberController {

    private final BarberService barberService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;

    @Operation(summary = "Meu perfil", description = "Retorna dados do barbeiro autentado (ID extraído do JWT).")
    @ApiResponse(responseCode = "200", description = "Dados do usuário")
    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.getProfile(barberId));
    }

    @Operation(summary = "Meus agendamentos", description = "Lista agendamentos ativos (AGENDADO e PROPOSTA_REAGENDAMENTO) do barbeiro.")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos")
    @GetMapping("/appointments")
    public ResponseEntity<List<AppointmentResponse>> myAppointments(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.myAppointments(barberId));
    }

    @Operation(summary = "Meu histórico", description = "Lista todos os agendamentos do barbeiro (inclui cancelados e finalizados).")
    @ApiResponse(responseCode = "200", description = "Lista de agendamentos")
    @GetMapping("/history")
    public ResponseEntity<List<AppointmentResponse>> myHistory(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.myHistory(barberId));
    }

    @Operation(summary = "Meus clientes", description = "Lista clientes vinculados a este barbeiro (assignedBarber).")
    @ApiResponse(responseCode = "200", description = "Lista de clientes")
    @GetMapping("/clients")
    public ResponseEntity<List<UserResponse>> listMyClients(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.listMyClients(barberId));
    }

    @Operation(summary = "Cancelar agendamento (barbeiro)", description = "Cancela agendamento. Mensagem opcional (ex.: imprevisto).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Cancelado com sucesso"),
        @ApiResponse(responseCode = "422", description = "Agendamento não é do barbeiro ou já cancelado", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<Void> cancelByBarber(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @RequestBody(required = false) CancelWithObservationRequest body) {
        Long barberId = getBarberId(request);
        String message = body != null ? body.getObservation() : null;
        barberService.cancelByBarber(barberId, id, message);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Finalizar atendimento", description = "Marca o agendamento como FINALIZADO.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Atendimento finalizado"),
        @ApiResponse(responseCode = "422", description = "Agendamento não pode ser finalizado", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @PutMapping("/appointments/{id}/finalize")
    public ResponseEntity<AppointmentResponse> finalizeAppointment(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.finalizeAppointment(barberId, id));
    }

    @Operation(summary = "Propor reagendamento", description = "Envia proposta de novo horário ao cliente (status PROPOSTA_REAGENDAMENTO).")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Proposta registrada"),
        @ApiResponse(responseCode = "422", description = "Horário ocupado ou agendamento inválido", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Agendamento não encontrado", content = @Content())
    })
    @PutMapping("/appointments/{id}/propose-reschedule")
    public ResponseEntity<AppointmentResponse> proposeReschedule(
            HttpServletRequest request,
            @Parameter(description = "ID do agendamento") @PathVariable Long id,
            @Valid @RequestBody ProposeRescheduleRequest body) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.proposeReschedule(barberId, id, body));
    }

    @Operation(summary = "Obter disponibilidade", description = "Retorna os horários de atendimento configurados por dia da semana (1=Segunda a 7=Domingo).")
    @ApiResponse(responseCode = "200", description = "Lista de disponibilidades")
    @GetMapping("/availability")
    public ResponseEntity<List<AvailabilityResponse>> getAvailability(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.getAvailability(barberId));
    }

    @Operation(summary = "Definir disponibilidade", description = "Substitui toda a disponibilidade do barbeiro. dayOfWeek: 1 a 7. endTime deve ser após startTime. Apenas o próprio barbeiro pode alterar.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Disponibilidade atualizada"),
        @ApiResponse(responseCode = "422", description = "Horário inválido (ex.: end antes de start)", content = @Content()),
        @ApiResponse(responseCode = "403", description = "Sem permissão", content = @Content())
    })
    @PutMapping("/availability")
    public ResponseEntity<List<AvailabilityResponse>> setAvailability(
            HttpServletRequest request,
            @Valid @RequestBody List<AvailabilityRequest> body) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.setAvailability(barberId, body));
    }

    @Operation(summary = "Dias de folga", description = "Retorna as datas em que o barbeiro não atende (folgas/feriados).")
    @ApiResponse(responseCode = "200", description = "Lista de datas")
    @GetMapping("/date-off")
    public ResponseEntity<List<LocalDate>> getDateOff(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.getDateOff(barberId));
    }

    @Operation(summary = "Definir dias de folga", description = "Substitui a lista de datas em que não atende. Reflete na tela do cliente (calendário).")
    @ApiResponse(responseCode = "200", description = "Lista atualizada de datas")
    @PutMapping("/date-off")
    public ResponseEntity<List<LocalDate>> setDateOff(
            HttpServletRequest request,
            @Valid @RequestBody BarberDateOffRequest body) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.setDateOff(barberId, body));
    }

    @Operation(summary = "Meus serviços", description = "Lista serviços ativos do barbeiro.")
    @ApiResponse(responseCode = "200", description = "Lista de serviços")
    @GetMapping("/services")
    public ResponseEntity<List<ServiceResponse>> listMyServices(HttpServletRequest request) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.listMyServices(barberId));
    }

    @Operation(summary = "Atualizar meu serviço", description = "Atualiza um serviço que pertence ao barbeiro.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Serviço atualizado"),
        @ApiResponse(responseCode = "422", description = "Serviço não pertence ao barbeiro", content = @Content()),
        @ApiResponse(responseCode = "404", description = "Serviço não encontrado", content = @Content())
    })
    @PutMapping("/services/{serviceId}")
    public ResponseEntity<ServiceResponse> updateMyService(
            HttpServletRequest request,
            @Parameter(description = "ID do serviço") @PathVariable Long serviceId,
            @Valid @RequestBody ServiceRequest body) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.updateMyService(barberId, serviceId, body));
    }

    @Operation(summary = "Atualizar intervalo da grade", description = "Define o intervalo em minutos para a grade de horários.")
    @ApiResponse(responseCode = "200", description = "Perfil atualizado")
    @PutMapping("/profile/slot-interval")
    public ResponseEntity<UserResponse> updateSlotInterval(
            HttpServletRequest request,
            @Valid @RequestBody com.americobarber.dto.request.SlotIntervalRequest body) {
        Long barberId = getBarberId(request);
        return ResponseEntity.ok(barberService.updateSlotInterval(barberId, body));
    }

    private Long getBarberId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        String token = auth != null && auth.startsWith("Bearer ") ? auth.substring(7) : "";
        Long userId = jwtUtil.getUserIdFromToken(token);
        User user = userRepository.findById(userId).orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        if (!Boolean.TRUE.equals(user.getIsBarber())) {
            throw new BusinessException("Acesso negado. Apenas admins barbeiros podem usar este painel.");
        }
        return userId;
    }
}
