package managment.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record FindAllUsersInGroupResponseDTO(
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
        String email,
        @JsonProperty("attributes")
        Map<String,String> attributes
){}