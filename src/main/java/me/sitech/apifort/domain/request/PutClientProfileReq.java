package me.sitech.apifort.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutClientProfileReq {

    @JsonProperty("client_profile_uuid")
    private String ClientProfileUuid;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("auth_claim_key")
    private String authClaimKey;
}
