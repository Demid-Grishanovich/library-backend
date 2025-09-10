package com.library.library_backend.service;

import com.library.library_backend.dto.RegisterRequest;
import com.library.library_backend.model.Role;
import com.library.library_backend.model.User;
import com.library.library_backend.repository.UserRepository;
import com.library.library_backend.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final JwtService jwt;

    public AuthService(UserRepository users, PasswordEncoder encoder, JwtService jwt) {
        this.users = users;
        this.encoder = encoder;
        this.jwt = jwt;
    }

    @Transactional
    public String register(RegisterRequest req) {
        if (users.existsByUsername(req.username())) {
            throw new IllegalArgumentException("Username already taken");
        }
        if (users.existsByEmail(req.email())) {
            throw new IllegalArgumentException("Email already used");
        }
        User user = User.builder()
                .username(req.username())
                .email(req.email())
                .passwordHash(encoder.encode(req.password()))
                .role(Role.USER)
                .build();
        users.save(user);
        return jwt.generate(user.getUsername());
    }
}
