package com.library.library_backend.dto;

import jakarta.validation.constraints.Size;



public record UpdateBookItemRequest(
        String title,
        String author,
        Integer year,
        String status   // 👈 добавили поле для BookStatus
) {}
