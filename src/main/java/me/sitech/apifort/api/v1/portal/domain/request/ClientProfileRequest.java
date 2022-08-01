package me.sitech.apifort.api.v1.portal.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileRequest {

    @JsonProperty("client_external_id")
    private String clientExternalId;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("auth_claim_key")
    private String authClaimKey;

    @JsonProperty("jwt_public_certificate")
    private String publicCertificate;

    @JsonProperty("realm")
    private String realm;
}
