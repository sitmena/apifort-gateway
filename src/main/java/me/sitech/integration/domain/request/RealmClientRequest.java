package me.sitech.integration.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class RealmClientRequest {

    @JsonProperty("realmName")
//    @NotNull(message = "realmName cannot be null")
//    @Size(min = 4, max = 200, message = "Password must be between 4 and 200 characters")
    private String realmName;
}
