package com.americobarber.controller;

import com.americobarber.dto.request.CreateBarberRequest;
import com.americobarber.dto.request.ServiceRequest;
import com.americobarber.dto.request.UserUpdateRequest;
import com.americobarber.dto.response.AppointmentResponse;
import com.americobarber.dto.response.ServiceResponse;
import com.americobarber.dto.response.UserResponse;
import com.americobarber.service.AdminService;
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

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Gestão por administrador. Requer ROLE_ADMIN (Bearer JWT). Barbeiros são admins com isBarber=true criados via POST /barbers.")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

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
}
