package me.sitech.apifort.domain.response.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileDetailsRes {

    @JsonProperty("client_profile_uuid")
    private String clientProfileUuid;

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("public_certificate")
    private String publicCertificate;

    @JsonProperty("realm")
    private String realm;

    @JsonProperty("auth_claim_key")
    private String authClaimKey;

    @JsonProperty("total_services")
    private Long totalServices;

    @JsonProperty("total_endpoints")
    private Long totalEndpoints;
}
