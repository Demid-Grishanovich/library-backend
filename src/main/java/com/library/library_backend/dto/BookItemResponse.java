package com.library.library_backend.dto;

import com.library.library_backend.model.BookItem;

public record BookItemResponse(
        Long id,
        String title,
        String author,
        Integer year,
        String inventoryCode,
        String qrToken,
        String status,
        Long addedByAdminId
) {
    public static BookItemResponse from(BookItem b) {
        return new BookItemResponse(
                b.getId(),
                b.getTitle(),
                b.getAuthor(),
                b.getYear(),
                b.getInventoryCode(),
                b.getQrToken(),
                b.getStatus().name(),
                b.getAddedByAdmin() != null ? b.getAddedByAdmin().getId() : null
        );
    }
}
