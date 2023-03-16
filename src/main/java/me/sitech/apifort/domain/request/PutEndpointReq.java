package me.sitech.apifort.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutEndpointReq {

    @JsonProperty(value = "endpoint_uuid")
    private String endpointUuid;

    @JsonProperty(value = "service_uuid")
    private String serviceUuid;

    @JsonProperty(value = "title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty(value ="endpoint_path")
    private String endpointPath;

    @JsonProperty(value ="method_type")
    private String methodType;

    @JsonProperty(value ="auth_claim_value")
    private String authClaimValue;

    @JsonProperty(value ="is_public_service")
    private boolean publicService;

    @JsonProperty(value ="offline_authentication")
    private boolean offlineAuthentication;

    @JsonProperty(value ="version_number")
    private Integer versionNumber;
}
