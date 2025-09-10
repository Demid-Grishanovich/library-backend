package com.library.library_backend.controller;

import com.library.library_backend.dto.AuthResponse;
import com.library.library_backend.dto.RegisterRequest;
import com.library.library_backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        String token = auth.register(request);
        return ResponseEntity.ok(new AuthResponse(token));
    }
}
