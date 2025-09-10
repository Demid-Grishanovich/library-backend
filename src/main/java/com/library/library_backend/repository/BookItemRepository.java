package com.library.library_backend.repository;

import com.library.library_backend.model.BookItem;
import com.library.library_backend.model.BookStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BookItemRepository extends JpaRepository<BookItem, Long> {

    @Query(
            value = """
            select b
            from BookItem b
            where b.addedByAdmin.id = :adminId
              and (:status is null or b.status = :status)
              and (
                    :q is null
                    or lower(b.title)         like lower(concat('%', :q, '%'))
                    or lower(b.author)        like lower(concat('%', :q, '%'))
                    or lower(b.inventoryCode) like lower(concat('%', :q, '%'))
              )
            """,
            countQuery = """
            select count(b)
            from BookItem b
            where b.addedByAdmin.id = :adminId
              and (:status is null or b.status = :status)
              and (
                    :q is null
                    or lower(b.title)         like lower(concat('%', :q, '%'))
                    or lower(b.author)        like lower(concat('%', :q, '%'))
                    or lower(b.inventoryCode) like lower(concat('%', :q, '%'))
              )
            """
    )
    Page<BookItem> findForAdmin(@Param("adminId") Long adminId,
                                @Param("status") BookStatus status,
                                @Param("q") String q,
                                Pageable pageable);

    Optional<BookItem> findByQrToken(String qrToken);

    boolean existsByAddedByAdmin_IdAndInventoryCode(Long adminId, String inventoryCode);
}
