package com.library.library_backend.dto;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record BulkConfirmRequest(@NotEmpty List<String> qrTokens) {}
