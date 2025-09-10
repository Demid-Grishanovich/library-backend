package com.library.library_backend.service;

import com.library.library_backend.dto.LoanResponse;
import com.library.library_backend.model.*;
import com.library.library_backend.repository.BookItemRepository;
import com.library.library_backend.repository.LoanRepository;
import com.library.library_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.EnumSet;
import java.util.List;

@Service
public class QrLoanService {

    private final BookItemRepository books;
    private final LoanRepository loans;
    private final UserRepository users;

    public QrLoanService(BookItemRepository books, LoanRepository loans, UserRepository users) {
        this.books = books;
        this.loans = loans;
        this.users = users;
    }

    @Transactional
    public LoanResponse takeByQr(String qr, Authentication auth, int days) {
        var user = users.findByUsername((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        BookItem book = books.findByQrToken(qr)
                .orElseThrow(() -> new IllegalArgumentException("Book not found by QR"));

        if (book.getStatus() == BookStatus.PENDING_RETURN) {
            loans.findCurrentByBook(book.getId()).ifPresent(l -> {
                l.setAdminConfirmedReturnAt(OffsetDateTime.now());
                l.setStatus(LoanStatus.RETURNED);
            });
        } else if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available");
        }

        OffsetDateTime now = OffsetDateTime.now();
        Loan loan = Loan.builder()
                .bookItem(book)
                .user(user)
                .borrowedAt(now)
                .dueAt(now.plusDays(days))
                .status(LoanStatus.ACTIVE)
                .build();

        book.setStatus(BookStatus.BORROWED);
        loans.save(loan);
        return toDto(loan);
    }

    @Transactional
    public LoanResponse markReturnByQr(String qr, Authentication auth) {
        var user = users.findByUsername((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        BookItem book = books.findByQrToken(qr)
                .orElseThrow(() -> new IllegalArgumentException("Book not found by QR"));

        Loan loan = loans.findCurrentByBook(book.getId())
                .orElseThrow(() -> new IllegalStateException("No active loan for this book"));

        if (!loan.getUser().getId().equals(user.getId())) {
            throw new IllegalStateException("This book is not on you");
        }
        loan.setUserMarkedReturnAt(OffsetDateTime.now());
        loan.setStatus(LoanStatus.PENDING_RETURN);
        book.setStatus(BookStatus.PENDING_RETURN);
        return toDto(loan);
    }

    @Transactional
    public LoanResponse confirmReturnByQr(String qr, Authentication auth) {
        var admin = users.findByUsername((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        if (admin.getRole() != Role.ADMIN) throw new IllegalStateException("Only ADMIN");

        BookItem book = books.findByQrToken(qr)
                .orElseThrow(() -> new IllegalArgumentException("Book not found by QR"));

        Loan loan = loans.findByBookItem_IdAndStatusIn(
                book.getId(), EnumSet.of(LoanStatus.ACTIVE, LoanStatus.PENDING_RETURN)
        ).orElseThrow(() -> new IllegalStateException("No open loan for this book"));

        loan.setAdminConfirmedReturnAt(OffsetDateTime.now());
        loan.setStatus(LoanStatus.RETURNED);
        book.setStatus(BookStatus.AVAILABLE);
        return toDto(loan);
    }

    @Transactional
    public int confirmReturnBulk(List<String> qrTokens, Authentication auth) {
        var admin = users.findByUsername((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalStateException("User not found"));
        if (admin.getRole() != Role.ADMIN) throw new IllegalStateException("Only ADMIN");

        int ok = 0;
        for (String qr : qrTokens) {
            var optBook = books.findByQrToken(qr);
            if (optBook.isEmpty()) continue;
            var book = optBook.get();

            var optLoan = loans.findByBookItem_IdAndStatusIn(
                    book.getId(), EnumSet.of(LoanStatus.ACTIVE, LoanStatus.PENDING_RETURN)
            );
            if (optLoan.isEmpty()) continue;

            var loan = optLoan.get();
            loan.setAdminConfirmedReturnAt(OffsetDateTime.now());
            loan.setStatus(LoanStatus.RETURNED);
            book.setStatus(BookStatus.AVAILABLE);
            ok++;
        }
        return ok;
    }

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
}
