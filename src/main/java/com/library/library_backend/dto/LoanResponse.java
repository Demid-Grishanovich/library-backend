package com.library.library_backend.dto;

import java.time.OffsetDateTime;

public record LoanResponse(
        Long id,
        Long bookItemId,
        Long userId,
        String status,
        OffsetDateTime borrowedAt,
        OffsetDateTime dueAt,
        OffsetDateTime userMarkedReturnAt,
        OffsetDateTime adminConfirmedReturnAt
) {}
