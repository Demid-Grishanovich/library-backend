package com.library.library_backend.web;

import com.library.library_backend.dto.UserDto;
import com.library.library_backend.model.User;
import com.library.library_backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserRepository users;

    public UserController(UserRepository users) {
        this.users = users;
    }

    @GetMapping("/me")
    public ResponseEntity<?> me() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Нет аутентификации или анонимный пользователь → 401
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return ResponseEntity.status(401).body("Unauthorized");
        }

        String username;
        Object principal = auth.getPrincipal();
        if (principal instanceof UserDetails ud) {
            username = ud.getUsername();
        } else {
            // Если в principal лежит просто строка
            username = String.valueOf(principal);
        }

        Optional<User> u = users.findByUsername(username);
        if (u.isEmpty()) {
            // токен валидный, но пользователя в БД нет → 404, а не 500
            return ResponseEntity.status(404).body("User not found");
        }

        User user = u.get();
        UserDto dto = new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name()
        );
        return ResponseEntity.ok(dto);
    }
}
