package com.library.library_backend.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "loan", indexes = {
        @Index(name = "idx_loan_user", columnList = "user_id"),
        @Index(name = "idx_loan_book", columnList = "book_item_id")
})
public class Loan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "book_item_id", nullable = false)
    private BookItem bookItem;

    @Column(name = "borrowed_at", nullable = false)
    private OffsetDateTime borrowedAt;

    @Column(name = "due_at")
    private OffsetDateTime dueAt;

    @Column(name = "user_marked_return_at")
    private OffsetDateTime userMarkedReturnAt;

    @Column(name = "admin_confirmed_return_at")
    private OffsetDateTime adminConfirmedReturnAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private LoanStatus status;

    @PrePersist
    void prePersist() {
        if (borrowedAt == null) borrowedAt = OffsetDateTime.now();
        if (status == null) status = LoanStatus.ACTIVE;
    }
}
