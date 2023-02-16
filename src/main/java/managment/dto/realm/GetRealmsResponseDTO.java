package managment.dto.realm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetRealmsResponseDTO(@JsonProperty("id")
                                   String id,
                                   @JsonProperty("realm")
                                   String realm,
                                   @JsonProperty("enabled")
                                   boolean enabled) {}

