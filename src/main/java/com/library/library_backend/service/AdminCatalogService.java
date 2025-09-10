package com.library.library_backend.service;

import com.library.library_backend.dto.AdminBookItemResponse;
import com.library.library_backend.model.BookItem;
import com.library.library_backend.model.BookStatus;
import com.library.library_backend.model.User;
import com.library.library_backend.repository.BookItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AdminCatalogService {

    private final BookItemRepository books;

    public AdminCatalogService(BookItemRepository books) {
        this.books = books;
    }

    /**
     * Список книг текущего админа с фильтрами и пагинацией.
     */
    public Page<AdminBookItemResponse> listForAdmin(User admin,
                                                    String status,
                                                    String query,
                                                    Pageable pageable) {
        BookStatus st = null;
        if (status != null && !status.isBlank()) {
            st = BookStatus.valueOf(status.trim().toUpperCase());
        }
        String q = (query == null || query.isBlank()) ? null : query.trim();

        Page<BookItem> page = books.findForAdmin(admin.getId(), st, q, pageable);
        return page.map(AdminBookItemResponse::from);
    }
}
