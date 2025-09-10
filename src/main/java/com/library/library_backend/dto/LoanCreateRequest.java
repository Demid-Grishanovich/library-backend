package com.library.library_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record LoanCreateRequest(
        @NotNull Long bookItemId,
        @NotNull Long userId,     // кому выдаём (можно выдать самому себе)
        @Min(1) int days          // на сколько дней
) {}
