package me.sitech.apifort.domain.response.endpoints;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class ClientEndpointDetailsResponse {

    @JsonProperty("endpoint_uuid")
    private String uuid;

    @JsonProperty("clients_profile_uuid")
    private String clientProfileFK;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("endpoint_path")
    private String endpointPath;

    @JsonProperty("endpoint_regex")
    private String endpointRegex;

    @JsonProperty("method_type")
    private String methodType;

    @JsonProperty("auth_claim_value")
    private String authClaimValue;

    @JsonProperty("offline_authentication")
    private boolean offlineAuthentication;

    @JsonProperty("version_number")
    private Integer versionNumber;
}
