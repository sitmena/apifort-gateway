package me.sitech.integration.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RealmGroupRequest {
    @JsonProperty("realmName")
    private String realmName;
    @JsonProperty("groupName")
    private String groupName;

}
