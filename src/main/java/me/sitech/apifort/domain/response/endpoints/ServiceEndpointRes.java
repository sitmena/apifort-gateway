package me.sitech.apifort.domain.response.endpoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceEndpointRes {

    @JsonProperty("endpoint_uuid")
    private String uuid;

}
