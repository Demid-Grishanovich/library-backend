// LoanStatusUpdateRequest.java
package com.library.library_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoanStatusUpdateRequest {
    private String status; // "RETURN_REQUESTED" | "RETURNED" | ...
}
