package com.library.library_backend.service;

import com.library.library_backend.dto.QrStateResponse;
import com.library.library_backend.model.BookItem;
import com.library.library_backend.model.BookStatus;
import com.library.library_backend.repository.BookItemRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class QrStateService {

    private final BookItemRepository books;

    public QrStateService(BookItemRepository books) {
        this.books = books;
    }

    public QrStateResponse state(String qrToken, Authentication auth) {
        BookItem book = books.findByQrToken(qrToken)
                .orElseThrow(() -> new IllegalArgumentException("Book not found by QR"));

        boolean authed = auth != null && auth.isAuthenticated();
        String action = "NONE";

        if (!authed) {
            return new QrStateResponse(book.getStatus().name(), true, action);
        }

        switch (book.getStatus()) {
            case AVAILABLE -> action = "TAKE";
            case PENDING_RETURN -> action = "TAKE"; // можно брать с полки
            case BORROWED -> action = "NONE"; // вернуть может только владелец, проверим на фронте по /me/loans
            default -> action = "NONE";
        }
        return new QrStateResponse(book.getStatus().name(), false, action);
    }
}
