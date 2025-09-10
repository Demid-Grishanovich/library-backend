package com.library.library_backend.repository;

import com.library.library_backend.model.Loan;
import com.library.library_backend.model.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Optional<Loan> findByBookItem_IdAndStatusIn(Long bookItemId, Iterable<LoanStatus> statuses);
    List<Loan> findByUser_IdAndStatusIn(Long userId, Iterable<LoanStatus> statuses);

    default Optional<Loan> findCurrentByBook(Long bookItemId) {
        return findByBookItem_IdAndStatusIn(
                bookItemId,
                EnumSet.of(LoanStatus.ACTIVE, LoanStatus.PENDING_RETURN)
        );
    }
}
