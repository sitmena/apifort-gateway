package me.sitech.apifort.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostEndpointRequest {


    @JsonProperty(value = "service_name")
    private String serviceName;

    @JsonProperty(value ="context_path")
    private String contextPath;

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
