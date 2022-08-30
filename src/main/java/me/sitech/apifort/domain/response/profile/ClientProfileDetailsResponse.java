package me.sitech.apifort.domain.response.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileDetailsResponse {

    @JsonProperty("clients_profile_uuid")
    private String clientProfileUuid;

    @JsonProperty("client_api_key")
    private String apiKey;

    @JsonProperty("client_public_certificate")
    private String publicCertificate;

    @JsonProperty("client_realm")
    private String realm;

    @JsonProperty("client_claim_key")
    private String authClaimKey;
}
