package me.sitech.apifort.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostClientProfileReq {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("auth_claim_key")
    private String authClaimKey;

    @JsonProperty("realm")
    private String realm;
}
