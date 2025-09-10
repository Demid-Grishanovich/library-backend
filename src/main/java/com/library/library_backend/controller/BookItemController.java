package com.library.library_backend.controller;

import com.library.library_backend.dto.BookItemResponse;
import com.library.library_backend.dto.CreateBookItemRequest;
import com.library.library_backend.dto.ErrorResponse;
import com.library.library_backend.dto.UpdateBookItemRequest;
import com.library.library_backend.service.BookItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookItemController {

    private final BookItemService service;

    public BookItemController(BookItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<BookItemResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public BookItemResponse one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateBookItemRequest req, Authentication auth) {
        try {
            return ResponseEntity.ok(service.create(req, auth));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody UpdateBookItemRequest req,
                                    Authentication auth) {
        try {
            return ResponseEntity.ok(service.update(id, req, auth));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        try {
            service.delete(id, auth);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage()));
        }
    }
}
