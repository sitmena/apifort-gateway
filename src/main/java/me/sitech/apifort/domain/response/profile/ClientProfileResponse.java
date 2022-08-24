package me.sitech.apifort.domain.response.profile;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClientProfileResponse {

    @JsonProperty("clients_profile_uuid")
    private String clientProfileUuid;
}
