package com.library.library_backend.controller;

import com.library.library_backend.dto.LoanCreateRequest;
import com.library.library_backend.dto.LoanResponse;
import com.library.library_backend.dto.LoanStatusUpdateRequest;
import com.library.library_backend.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "loan-controller")
@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loans;

    public LoanController(LoanService loans) {
        this.loans = loans;
    }

    // создать займ (взять книгу) — тут POST по смыслу корректен
    @Operation(summary = "Оформить выдачу (взять книгу)")
    @PostMapping
    public ResponseEntity<LoanResponse> take(@RequestBody LoanCreateRequest r,
                                             Authentication auth) {
        return ResponseEntity.ok(loans.take(r, auth));
    }

    // ИМЕННО ЭТО: обновление статуса существующего займа — PATCH
    @Operation(summary = "Обновить статус займа (возврат/подтверждение)")
    @PatchMapping("/{id}")
    public ResponseEntity<LoanResponse> updateStatus(@PathVariable Long id,
                                                     @RequestBody LoanStatusUpdateRequest r,
                                                     Authentication auth) {
        return ResponseEntity.ok(loans.updateStatus(id, r, auth));
    }

    // массовое подтверждение возврата — PATCH коллекции
    @Operation(summary = "Массовое подтверждение возврата")
    @PatchMapping("/bulk")
    public ResponseEntity<List<LoanResponse>> bulkConfirmReturn(@RequestBody List<Long> ids,
                                                                Authentication auth) {
        return ResponseEntity.ok(loans.bulkConfirmReturn(ids, auth));
    }

    // --- ВРЕМЕННО: чтобы не ломать фронт, можно оставить старые POST как deprecated ---
    @Deprecated
    @PostMapping("/{id}/mark-return")
    public ResponseEntity<LoanResponse> markReturn_DEPRECATED(@PathVariable Long id,
                                                              Authentication auth) {
        LoanStatusUpdateRequest r = new LoanStatusUpdateRequest("RETURN_REQUESTED");
        return ResponseEntity.ok(loans.updateStatus(id, r, auth));
    }

    @Deprecated
    @PostMapping("/{id}/confirm-return")
    public ResponseEntity<LoanResponse> confirmReturn_DEPRECATED(@PathVariable Long id,
                                                                 Authentication auth) {
        LoanStatusUpdateRequest r = new LoanStatusUpdateRequest("RETURNED");
        return ResponseEntity.ok(loans.updateStatus(id, r, auth));
    }
}
