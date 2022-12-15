package me.sitech.integration.domain.response.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorRes {
    private String traceId;
    private String message;
}
