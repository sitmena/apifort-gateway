package me.sitech.apifort.api.v1.portal.domain.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DefaultResponse {
    private Integer code;
    private String message;
}
