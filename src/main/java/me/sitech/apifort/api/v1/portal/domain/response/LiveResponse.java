package me.sitech.apifort.api.v1.portal.domain.response;

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
