package managment.dto.realm;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GetRealmGroupsResponseDTO(@JsonProperty("id") String id,
                                        @JsonProperty("name")
                                        String name) {}
