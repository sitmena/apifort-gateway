package me.sitech.apifort.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEndpointRequest {

    @JsonProperty("clients_profile_uuid")
    private String clientProfileFK;

    @JsonProperty("api_key")
    private String apiKey;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("context_path")
    private String contextPath;

    @JsonProperty("endpoint_path")
    private String endpointPath;

    @JsonProperty("method_type")
    private String methodType;

    @JsonProperty("auth_claim_value")
    private String authClaimValue;

    @JsonProperty("is_public_service")
    private boolean publicEndpoint;

    @JsonProperty("offline_authentication")
    private boolean offlineAuthentication;

    @JsonProperty("version_number")
    private Integer versionNumber;

}
