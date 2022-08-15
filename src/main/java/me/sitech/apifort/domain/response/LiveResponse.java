package me.sitech.apifort.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LiveResponse {
    @JsonProperty("server_time")
    private String serverTime;

    @JsonProperty("status")
    private String status;
}
