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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isBarber(Boolean.TRUE.equals(user.getIsBarber()))
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
                .build();
        user = userRepository.save(user);
        String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getRole());
        log.info("User registered: id={}", user.getId());
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .isBarber(Boolean.TRUE.equals(user.getIsBarber()))
                .build();
    }
}
