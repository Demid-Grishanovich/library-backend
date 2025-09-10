package com.library.library_backend.controller;

import com.library.library_backend.dto.BookItemResponse;
import com.library.library_backend.repository.BookItemRepository;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/books")
public class BookQrController {

    private final com.library.library_backend.service.BookItemService service;

    public BookQrController(com.library.library_backend.service.BookItemService service) {
        this.service = service;
    }

    @GetMapping("/qr/{qrToken}")
    public BookItemResponse byQr(@PathVariable String qrToken) {
        var item = service.findAll().stream()
                .filter(b -> b.qrToken().equals(qrToken))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Book not found by QR"));
        return item;
    }
}
