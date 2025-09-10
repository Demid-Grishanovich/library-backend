package com.library.library_backend.controller;

import com.library.library_backend.dto.AdminBookItemResponse;
import com.library.library_backend.model.User;
import com.library.library_backend.service.AdminCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/books")
public class AdminCatalogController {

    private final AdminCatalogService service;

    public AdminCatalogController(AdminCatalogService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Список книг админа с пагинацией/фильтрами")
    public Page<AdminBookItemResponse> list(@AuthenticationPrincipal User admin,
                                            @RequestParam(required = false) String status,
                                            @RequestParam(required = false, name = "q") String query,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, Math.min(size, 200));
        return service.listForAdmin(admin, status, query, pageable);
    }
}
