package com.library.library_backend.dto;

import java.time.OffsetDateTime;

public record MyLoanResponse(
        Long id,
        Long bookItemId,
        String title,
        String author,
        String status,
        OffsetDateTime borrowedAt,
        OffsetDateTime dueAt
) {}
