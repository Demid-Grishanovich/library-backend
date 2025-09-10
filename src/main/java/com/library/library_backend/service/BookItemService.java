package com.library.library_backend.service;

import com.library.library_backend.dto.BookItemResponse;
import com.library.library_backend.dto.CreateBookItemRequest;
import com.library.library_backend.dto.UpdateBookItemRequest;
import com.library.library_backend.model.BookItem;
import com.library.library_backend.model.BookStatus;
import com.library.library_backend.model.Role;
import com.library.library_backend.model.User;
import com.library.library_backend.repository.BookItemRepository;
import com.library.library_backend.repository.UserRepository;
import com.library.library_backend.util.QrTokenGenerator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookItemService {

    private final BookItemRepository books;
    private final UserRepository users;

    public BookItemService(BookItemRepository books, UserRepository users) {
        this.books = books;
        this.users = users;
    }

    public List<BookItemResponse> findAll() {
        return books.findAll().stream().map(this::toDto).toList();
    }

    public BookItemResponse findById(Long id) {
        var item = books.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));
        return toDto(item);
    }

    @Transactional
    public BookItemResponse create(CreateBookItemRequest req, Authentication auth) {
        User admin = currentUser(auth);
        requireAdmin(admin);

        // records → акцессоры без префикса get*
        if (books.existsByAddedByAdmin_IdAndInventoryCode(admin.getId(), req.inventoryCode())) {
            throw new IllegalArgumentException("Inventory code already exists for this admin");
        }

        String qr = QrTokenGenerator.generate();

        BookItem item = BookItem.builder()
                .title(req.title())
                .author(req.author())
                .year(req.year())
                .inventoryCode(req.inventoryCode())
                .qrToken(qr)
                .status(BookStatus.AVAILABLE)
                .addedByAdmin(admin)
                .build();

        books.save(item);
        return toDto(item);
    }

    @Transactional
    public BookItemResponse update(Long id, UpdateBookItemRequest req, Authentication auth) {
        User admin = currentUser(auth);
        requireAdmin(admin);

        BookItem item = books.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (!item.getAddedByAdmin().getId().equals(admin.getId())) {
            throw new IllegalStateException("You cannot modify a book created by another admin");
        }

        // Update* у тебя тоже record → акцессоры title()/author()/year()/status()
        if (req.title()  != null && !req.title().isBlank())  item.setTitle(req.title());
        if (req.author() != null && !req.author().isBlank()) item.setAuthor(req.author());
        if (req.year()   != null)                             item.setYear(req.year());
        if (req.status() != null && !req.status().isBlank()) {
            item.setStatus(BookStatus.valueOf(req.status().trim().toUpperCase()));
        }

        return toDto(item);
    }

    @Transactional
    public void delete(Long id, Authentication auth) {
        User admin = currentUser(auth);
        requireAdmin(admin);

        BookItem item = books.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Book not found: " + id));

        if (!item.getAddedByAdmin().getId().equals(admin.getId())) {
            throw new IllegalStateException("You cannot delete a book created by another admin");
        }
        books.delete(item);
    }

    private BookItemResponse toDto(BookItem b) {
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

    private User currentUser(Authentication auth) {
        // Если у тебя репозиторий ищет по email — замени на findByEmail(...)
        String username = (String) auth.getPrincipal();
        return users.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found: " + username));
    }

    private void requireAdmin(User u) {
        if (u.getRole() != Role.ADMIN) {
            throw new IllegalStateException("Only ADMIN");
        }
    }
}
