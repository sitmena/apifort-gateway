package me.sitech.apifort.domain.response.common;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultResponse {
    private Integer code;
    private String message;
}
