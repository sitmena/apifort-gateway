package me.sitech.apifort.domain.response.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveRes {
    @JsonProperty("server_time")
    private String serverTime;

    @JsonProperty("status")
    private String status;
}
