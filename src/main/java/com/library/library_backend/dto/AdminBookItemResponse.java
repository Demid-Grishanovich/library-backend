package com.library.library_backend.dto;

import com.library.library_backend.model.BookItem;
import com.library.library_backend.model.BookStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
public class AdminBookItemResponse {
    Long id;
    String title;
    String author;
    Integer year;
    String inventoryCode;
    String qrToken;
    BookStatus status;
    Long addedByAdminId;
    Long borrowerId;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;

    public static AdminBookItemResponse fromEntity(BookItem b) {
        return AdminBookItemResponse.builder()
                .id(b.getId())
                .title(b.getTitle())
                .author(b.getAuthor())
                .year(b.getYear())
                .inventoryCode(b.getInventoryCode())
                .qrToken(b.getQrToken())
                .status(b.getStatus())
                .addedByAdminId(b.getAddedByAdmin() != null ? b.getAddedByAdmin().getId() : null)
                .borrowerId(b.getBorrower() != null ? b.getBorrower().getId() : null)
                .createdAt(b.getCreatedAt())
                .updatedAt(b.getUpdatedAt())
                .build();
    }

    /** Алиас под method reference в AdminCatalogService */
    public static AdminBookItemResponse from(BookItem b) {
        return fromEntity(b);
    }
}
