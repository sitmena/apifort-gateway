package me.sitech.apifort.domain.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private String traceId;
    private String message;
}
