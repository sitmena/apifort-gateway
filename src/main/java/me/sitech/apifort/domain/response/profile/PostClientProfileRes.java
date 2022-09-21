package me.sitech.apifort.domain.response.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostClientProfileRes {

    @JsonProperty("client_profile_uuid")
    private String clientProfileUuid;
}
