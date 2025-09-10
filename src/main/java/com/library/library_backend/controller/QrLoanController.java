package com.library.library_backend.controller;

import com.library.library_backend.dto.LoanResponse;
import com.library.library_backend.dto.LoanStatusUpdateRequest;
import com.library.library_backend.service.QrLoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "qr-loan-controller")
@RestController
@RequestMapping("/api/loans/qr")
public class QrLoanController {

    private final QrLoanService qrLoans;

    public QrLoanController(QrLoanService qrLoans) {
        this.qrLoans = qrLoans;
    }

    // QrLoanController.java
    @PostMapping("/{qrToken}/take")
    public ResponseEntity<LoanResponse> takeByQr(@PathVariable String qrToken,
                                                 @RequestParam(name = "days", defaultValue = "14") int days,
                                                 Authentication auth) {
        return ResponseEntity.ok(qrLoans.takeByQr(qrToken, auth, days));
    }


    // обновления статуса по QR — PATCH
    @Operation(summary = "Отметить возврат по QR")
    @PatchMapping("/{qrToken}/mark-return")
    public ResponseEntity<LoanResponse> markReturnByQr(@PathVariable String qrToken,
                                                       Authentication auth) {
        return ResponseEntity.ok(qrLoans.markReturnByQr(qrToken, auth));
    }

    @Operation(summary = "Подтвердить возврат по QR")
    @PatchMapping("/{qrToken}/confirm-return")
    public ResponseEntity<LoanResponse> confirmReturnByQr(@PathVariable String qrToken,
                                                          Authentication auth) {
        return ResponseEntity.ok(qrLoans.confirmReturnByQr(qrToken, auth));
    }
}
