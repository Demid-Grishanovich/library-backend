package com.library.library_backend.dto;

public record QrStateResponse(
        String status,     // AVAILABLE | BORROWED | PENDING_RETURN | LOST (на будущее)
        boolean needsAuth, // true если не авторизован
        String action      // TAKE | RETURN | NONE
) {}
