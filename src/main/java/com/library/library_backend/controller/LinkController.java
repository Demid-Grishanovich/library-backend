package com.library.library_backend.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/l")
public class LinkController {

    @Value("${app.frontend-base-url:http://localhost:5173}")
    private String front;

    @GetMapping("/{qrToken}")
    public ResponseEntity<Void> open(@PathVariable String qrToken) {
        // фронт сам дернёт GET /api/qr/{qrToken} и покажет «взять/сдать»
        URI target = URI.create(front + "/qr/" + qrToken);
        return ResponseEntity.status(302).location(target).build();
    }
}
