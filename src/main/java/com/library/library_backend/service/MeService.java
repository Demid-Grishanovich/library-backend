package com.library.library_backend.service;

import com.library.library_backend.dto.MyLoanResponse;
import com.library.library_backend.model.LoanStatus;
import com.library.library_backend.repository.LoanRepository;
import com.library.library_backend.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
public class MeService {
    private final LoanRepository loans;
    private final UserRepository users;

    public MeService(LoanRepository loans, UserRepository users) {
        this.loans = loans;
        this.users = users;
    }

    public List<MyLoanResponse> myLoans(Authentication auth) {
        var user = users.findByUsername((String) auth.getPrincipal())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        return loans.findByUser_IdAndStatusIn(
                user.getId(), EnumSet.of(LoanStatus.ACTIVE, LoanStatus.PENDING_RETURN)
        ).stream().map(l -> new MyLoanResponse(
                l.getId(),
                l.getBookItem().getId(),
                l.getBookItem().getTitle(),
                l.getBookItem().getAuthor(),
                l.getStatus().name(),
                l.getBorrowedAt(),
                l.getDueAt()
        )).toList();
    }
}
