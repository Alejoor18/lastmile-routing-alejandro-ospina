package com.alejandro.lastmile.service;

import com.alejandro.lastmile.domain.User;
import com.alejandro.lastmile.domain.enums.UserRole;
import com.alejandro.lastmile.dto.AuthResponse;
import com.alejandro.lastmile.dto.LoginRequest;
import com.alejandro.lastmile.dto.RegisterRequest;
import com.alejandro.lastmile.dto.UserResponse;
import com.alejandro.lastmile.exception.BadRequestException;
import com.alejandro.lastmile.exception.ResourceNotFoundException;
import com.alejandro.lastmile.repository.UserRepository;
import com.alejandro.lastmile.security.JwtService;
import java.util.Locale;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Ya existe un usuario registrado con ese email");
        }

        User user = new User(
                request.fullName().trim(),
                email,
                passwordEncoder.encode(request.password()),
                UserRole.USER
        );
        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);
        return toAuthResponse(saved, token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        String email = normalizeEmail(request.email());
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, request.password()));
        } catch (BadCredentialsException ex) {
            throw new BadRequestException("Email o contraseña incorrectos");
        }

        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadRequestException("Email o contraseña incorrectos"));
        String token = jwtService.generateToken(user);
        return toAuthResponse(user, token);
    }

    @Transactional(readOnly = true)
    public UserResponse currentUser(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return DtoMapper.toUserResponse(user);
    }

    private AuthResponse toAuthResponse(User user, String token) {
        return new AuthResponse(token, "Bearer", user.getId(), user.getFullName(), user.getEmail(), user.getRole());
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.toLowerCase(Locale.ROOT).trim();
    }
}
