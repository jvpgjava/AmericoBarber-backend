package com.americobarber.controller;

import com.americobarber.dto.request.LoginRequest;
import com.americobarber.dto.request.RegisterRequest;
import com.americobarber.dto.response.LoginResponse;
import com.americobarber.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Login e cadastro (públicos, sem Bearer). Cadastro cria sempre ROLE_CLIENT; barbeiros são criados pelo admin em POST /api/admin/barbers.")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Autentica por email e senha. Retorna JWT e dados do usuário.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Login realizado com sucesso"),
        @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Dados inválidos", content = @Content())
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Operation(summary = "Cadastro", description = "Registra novo cliente (sempre ROLE_CLIENT). Envie role: ROLE_CLIENT. Barbeiros não são criados aqui; use POST /api/admin/barbers.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Cadastro realizado; retorna token e dados do usuário"),
        @ApiResponse(responseCode = "422", description = "Email, CPF ou telefone já cadastrado", content = @Content()),
        @ApiResponse(responseCode = "400", description = "Validação falhou", content = @Content())
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }
}
