package me.sitech.apifort.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEndpointRequest {

    @JsonProperty(value = "clients_profile_uuid",required = true)
    private String clientProfileFK;

    @JsonProperty(value = "api_key",required = true)
    private String apiKey;

    @JsonProperty(value = "service_name",required = true)
    private String serviceName;

    @JsonProperty(value ="context_path",required = true)
    private String contextPath;

    @JsonProperty(value ="endpoint_path",required = true)
    private String endpointPath;

    @JsonProperty(value ="method_type",required = true)
    private String methodType;

    @JsonProperty(value ="auth_claim_value",required = true)
    private String authClaimValue;

    @JsonProperty(value ="is_public_service",required = true)
    private boolean publicEndpoint;

    @JsonProperty(value ="offline_authentication",required = true)
    private boolean offlineAuthentication;

    @JsonProperty(value ="version_number",required = true)
    private Integer versionNumber;

}
