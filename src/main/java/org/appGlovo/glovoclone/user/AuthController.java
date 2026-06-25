package org.appGlovo.glovoclone.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.appGlovo.glovoclone.user.dto.AuthResponse;
import org.appGlovo.glovoclone.user.dto.LoginRequest;
import org.appGlovo.glovoclone.user.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}