package com.library.library_backend.service;

import com.library.library_backend.dto.LoginRequest;
import com.library.library_backend.model.User;
import com.library.library_backend.repository.UserRepository;
import com.library.library_backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class LoginService {
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public LoginService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public String login(LoginRequest req) {
        User u = users.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!encoder.matches(req.password(), u.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid password");
        }
        return jwt.generate(u.getUsername());
    }
}
