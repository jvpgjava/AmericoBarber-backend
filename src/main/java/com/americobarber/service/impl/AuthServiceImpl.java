package com.americobarber.service.impl;

import com.americobarber.dto.request.LoginRequest;
import com.americobarber.dto.request.RegisterRequest;
import com.americobarber.dto.response.LoginResponse;
import com.americobarber.entity.User;
import com.americobarber.exception.BusinessException;
import com.americobarber.repository.UserRepository;
import com.americobarber.service.AuthService;
import com.americobarber.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.americobarber.entity.ConfirmationToken;
import com.americobarber.repository.ConfirmationTokenRepository;
import com.americobarber.service.EmailService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final ConfirmationTokenRepository confirmationTokenRepository;
    private final EmailService emailService;

    @Value("${app.frontend.url:http://localhost:5173}")
    private String frontendUrl;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        log.info("Tentativa de LOGIN: Email={}, Role={}, Verified={}", user.getEmail(), user.getRole(), user.getEmailVerified());

        if (com.americobarber.enums.UserRole.ROLE_CLIENT.equals(user.getRole())) {
            // Checagem rigorosa: emailVerified tem que ser TRUE
            if (user.getEmailVerified() == null || !user.getEmailVerified()) {
                log.warn("BLOQUEADO: E-mail não verificado para {}", user.getEmail());
                throw new BusinessException("Por favor, confirme seu email antes de fazer login.");
            }
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
                
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .phone(user.getPhone())
                .role(user.getRole())
                .isBarber(Boolean.TRUE.equals(user.getIsBarber()))
                .isOwner(Boolean.TRUE.equals(user.getIsOwner()))
                .profilePicture(user.getProfilePicture())
                .description(user.getDescription())
                .descriptionUpdatedAt(user.getDescriptionUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (request.getRole() == null) {
            throw new BusinessException("Papel do usuário é obrigatório");
        }
        if (request.getRole() != com.americobarber.enums.UserRole.ROLE_CLIENT) {
            throw new BusinessException("Cadastro público permite apenas papel de cliente. Barbeiros são criados pelo admin.");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Email já cadastrado");
        }
        if (userRepository.existsByCpf(request.getCpf())) {
            throw new BusinessException("CPF já cadastrado");
        }
        if (userRepository.existsByPhone(request.getPhone())) {
            throw new BusinessException("Telefone já cadastrado");
        }
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .cpf(request.getCpf())
                .phone(request.getPhone())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .active(true)
                .emailVerified(false)
                .build();
        user = userRepository.save(user);

        // Gera token de confirmação
        String tokenString = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = ConfirmationToken.builder()
                .token(tokenString)
                .user(user)
                .expiresAt(Instant.now().plus(24, ChronoUnit.HOURS))
                .build();
        confirmationTokenRepository.save(confirmationToken);

        // Envia email de confirmação assíncrono
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", user.getName());
        variables.put("confirmationLink", frontendUrl + "/confirm-email?token=" + tokenString);
        emailService.sendHtmlEmail(user.getEmail(), "Confirme seu E-mail - Américo Barber Club", "email-confirmation", variables);

        log.info("User registered: id={}, email={}, verification email sent", user.getId(), user.getEmail());
        
        // Retornamos dados básicos, mas SEM token JWT para forçar login após confirmação
        return LoginResponse.builder()
                .token(null) // Força o usuário a não estar logado imediatamente
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cpf(user.getCpf())
                .phone(user.getPhone())
                .role(user.getRole())
                .build();
    }

    @Override
    @Transactional
    public void confirmEmail(String tokenString) {
        ConfirmationToken token = confirmationTokenRepository.findByToken(tokenString)
                .orElseThrow(() -> new BusinessException("Token inválido ou não encontrado."));

        if (token.isExpired()) {
            throw new BusinessException("Token expirado.");
        }

        User user = token.getUser();
        user.setEmailVerified(true);
        userRepository.save(user);

        confirmationTokenRepository.delete(token);
        log.info("Email verificado para o usuário: id={}", user.getId());
    }
}
