package me.sitech.apifort.api.v1.portal.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientEndpointResponse {

    @JsonProperty("uuid")
    private String uuid;
}
