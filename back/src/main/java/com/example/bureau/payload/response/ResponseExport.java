package com.example.bureau.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class ResponseExport {
    private final boolean success;
    private final String message;
}
