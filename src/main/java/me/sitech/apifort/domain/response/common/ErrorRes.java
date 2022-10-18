package me.sitech.apifort.domain.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorRes {
    private String traceId;
    private String message;
}
