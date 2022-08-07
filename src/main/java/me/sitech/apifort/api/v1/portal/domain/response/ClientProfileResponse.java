package me.sitech.apifort.api.v1.portal.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileResponse {

    @JsonProperty("client_external_id")
    private String clientExternalId;

    @JsonProperty("auth_claim_key")
    private String authClaimKey;

/*    @JsonProperty("auth_claim_value")
    private String authClaimValue;*/

    @JsonProperty("realm")
    private String realm;
}
