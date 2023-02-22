package me.sitech.apifort.domain.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorRes {
    @JsonProperty("trace_id")
    private String traceId;

    @JsonProperty("message")
    private String message;
}
