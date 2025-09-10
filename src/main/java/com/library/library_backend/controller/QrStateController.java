package com.library.library_backend.controller;

import com.library.library_backend.dto.QrStateResponse;
import com.library.library_backend.service.QrStateService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class QrStateController {
    private final QrStateService service;

    public QrStateController(QrStateService service) {
        this.service = service;
    }

    // публичный: если без токена, вернёт needsAuth=true
    @GetMapping("/qr/{qrToken}")
    public QrStateResponse state(@PathVariable String qrToken,
                                 @Parameter(hidden = true) Authentication auth) {
        return service.state(qrToken, auth);
    }
}
