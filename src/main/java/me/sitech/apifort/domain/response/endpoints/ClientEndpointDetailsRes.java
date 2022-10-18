package me.sitech.apifort.domain.response.endpoints;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Date;

@Data
@Builder
@With
@AllArgsConstructor
@NoArgsConstructor
public class ClientEndpointDetailsRes {

    @JsonProperty("endpoint_uuid")
    private String uuid;

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

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("created_date")
    private Date createdDate ;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("updated_date")
    private Date updatedDate;
}
