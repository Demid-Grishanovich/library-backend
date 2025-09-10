package com.library.library_backend.controller;

import com.library.library_backend.dto.MyLoanResponse;
import com.library.library_backend.service.MeService;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class MeController {
    private final MeService me;

    public MeController(MeService me) {
        this.me = me;
    }

    @GetMapping("/loans")
    public List<MyLoanResponse> myLoans(
            @Parameter(hidden = true) Authentication auth
    ) {
        return me.myLoans(auth);
    }
}
