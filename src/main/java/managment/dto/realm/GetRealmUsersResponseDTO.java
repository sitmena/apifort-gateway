package managment.dto.realm;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetRealmUsersResponseDTO(
        @JsonProperty("id")
        String id,
        @JsonProperty("createdTimestamp")
        long createdTimestamp,
        @JsonProperty("userName")
        String username,
        @JsonProperty("enabled")
        boolean enabled,
        @JsonProperty("firstName")
        String firstName,
        @JsonProperty("lastName")
        String lastName,
        @JsonProperty("email")
        String email) {}
