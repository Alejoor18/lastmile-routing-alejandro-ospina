package com.alejandro.lastmile.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.alejandro.lastmile.dto.AuthResponse;
import com.alejandro.lastmile.dto.LoginRequest;
import com.alejandro.lastmile.dto.RegisterRequest;
import com.alejandro.lastmile.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void registersAndLogsInUserWithJwt() {
        AuthResponse registered = authService.register(new RegisterRequest(
                "Alejandro Test",
                "alejandro@test.com",
                "Admin123*"
        ));

        AuthResponse loggedIn = authService.login(new LoginRequest("alejandro@test.com", "Admin123*"));

        assertThat(registered.token()).isNotBlank();
        assertThat(loggedIn.token()).isNotBlank();
        assertThat(loggedIn.email()).isEqualTo("alejandro@test.com");
        assertThat(loggedIn.tokenType()).isEqualTo("Bearer");
    }
}
