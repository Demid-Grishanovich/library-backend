package com.library.library_backend.service;

import com.library.library_backend.dto.LoanCreateRequest;
import com.library.library_backend.dto.LoanResponse;
import com.library.library_backend.dto.LoanStatusUpdateRequest;
import com.library.library_backend.model.BookItem;
import com.library.library_backend.model.BookStatus;
import com.library.library_backend.model.Loan;
import com.library.library_backend.model.LoanStatus;
import com.library.library_backend.model.Role;
import com.library.library_backend.model.User;
import com.library.library_backend.repository.BookItemRepository;
import com.library.library_backend.repository.LoanRepository;
import com.library.library_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

@Service
public class LoanService {

    private final LoanRepository loans;
    private final BookItemRepository books;
    private final UserRepository users;

    public LoanService(LoanRepository loans, BookItemRepository books, UserRepository users) {
        this.loans = loans;
        this.books = books;
        this.users = users;
    }

    // ====== ПУБЛИЧНЫЕ МЕТОДЫ, КОТОРЫЕ ЖДЁТ КОНТРОЛЛЕР ======

    /** Оформить выдачу (взять книгу). */
    @Transactional
    public LoanResponse take(LoanCreateRequest req, Authentication auth) {
        return create(req, auth);
    }

    /** Обновить статус займа (через PATCH). */
    @Transactional
    public LoanResponse updateStatus(Long loanId, LoanStatusUpdateRequest r, Authentication auth) {
        String status = r.getStatus();
        if (status == null) {
            throw new IllegalArgumentException("status is required");
        }
        switch (status.trim().toUpperCase()) {
            case "RETURN_REQUESTED":   // пользователь отметил возврат
                return markReturn(loanId, auth);
            case "RETURNED":           // администратор подтвердил возврат
                return confirmReturn(loanId, auth);
            default:
                throw new IllegalArgumentException("Unsupported status: " + status);
        }
    }

    /** Массовое подтверждение возврата. */
    @Transactional
    public List<LoanResponse> bulkConfirmReturn(List<Long> ids, Authentication auth) {
        List<LoanResponse> result = new ArrayList<>();
        if (ids == null || ids.isEmpty()) return result;
        for (Long id : ids) {
            result.add(confirmReturn(id, auth));
        }
        return result;
    }

    // ====== ВНУТРЕННЯЯ ЛОГИКА (была у тебя, оставляю + чуть правлю) ======

    /** Создать запись займа (взять книгу). */
    @Transactional
    public LoanResponse create(LoanCreateRequest req, Authentication auth) {
        User admin = currentUser(auth);
        requireAdmin(admin);

        BookItem book = books.findById(req.bookItemId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available");
        }

        // не допускаем параллельных активных займов для одной книги
        loans.findByBookItem_IdAndStatusIn(
                book.getId(), EnumSet.of(LoanStatus.ACTIVE, LoanStatus.PENDING_RETURN)
        ).ifPresent(l -> { throw new IllegalStateException("Book already loaned"); });

        User reader = users.findById(req.userId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        OffsetDateTime now = OffsetDateTime.now();

        Loan loan = Loan.builder()
                .bookItem(book)
                .user(reader)
                .borrowedAt(now)
                .dueAt(now.plusDays(req.days()))
                .status(LoanStatus.ACTIVE)
                .build();

        // книга стала "унесённой"
        book.setStatus(BookStatus.BORROWED);

        loans.save(loan);
        return toDto(loan);
    }

    /** Пользователь помечает возврат. */
    @Transactional
    public LoanResponse markReturn(Long loanId, Authentication auth) {
        User user = currentUser(auth);

        Loan loan = loans.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (!loan.getUser().getId().equals(user.getId()) && user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("You cannot mark return for this loan");
        }
        if (loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan is not ACTIVE");
        }

        loan.setUserMarkedReturnAt(OffsetDateTime.now());
        loan.setStatus(LoanStatus.PENDING_RETURN);
        loan.getBookItem().setStatus(BookStatus.PENDING_RETURN);

        return toDto(loan);
    }

    /** Администратор подтверждает возврат. */
    @Transactional
    public LoanResponse confirmReturn(Long loanId, Authentication auth) {
        User admin = currentUser(auth);
        requireAdmin(admin);

        Loan loan = loans.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Loan not found"));

        if (loan.getStatus() != LoanStatus.PENDING_RETURN && loan.getStatus() != LoanStatus.ACTIVE) {
            throw new IllegalStateException("Loan has wrong status for confirmation");
        }

        loan.setAdminConfirmedReturnAt(OffsetDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        loan.getBookItem().setStatus(BookStatus.AVAILABLE);

        return toDto(loan);
    }

    // ====== утилиты ======

    private LoanResponse toDto(Loan l) {
        return new LoanResponse(
                l.getId(),
                l.getBookItem().getId(),
                l.getUser().getId(),
                l.getStatus().name(),
                l.getBorrowedAt(),
                l.getDueAt(),
                l.getUserMarkedReturnAt(),
                l.getAdminConfirmedReturnAt()
        );
    }

    /** Достаём текущего пользователя по Authentication.
     *  ПРИМЕЧАНИЕ: если у тебя в UserRepository метод называется иначе
     *  (например, findByEmail), поменяй его здесь.
     */
    private User currentUser(Authentication auth) {
        String username = (String) auth.getPrincipal();
        return users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));
    }

    private void requireAdmin(User u) {
        if (u.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only ADMIN can perform this action");
        }
    }
}
