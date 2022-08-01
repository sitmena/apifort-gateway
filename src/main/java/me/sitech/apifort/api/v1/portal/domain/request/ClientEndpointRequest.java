package me.sitech.apifort.api.v1.portal.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientEndpointRequest {

    @JsonProperty("clients_profile_uuid")
    private String clientProfileFK;

    @JsonProperty("service_name")
    private String serviceName;

    @JsonProperty("endpoint_path")
    private String endpointPath;

    @JsonProperty("method_type")
    private String methodType;

    @JsonProperty("auth_claim_value")
    private String authClaimValue;

    @JsonProperty("offline_authentication")
    private boolean offlineAuthentication;

    @JsonProperty("version_number")
    private Integer versionNumber;

}
