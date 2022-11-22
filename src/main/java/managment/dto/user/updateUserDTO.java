package managment.dto.user;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
public class updateUserDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("realmName")
    private String realmName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userId")
    private String userId;
    @JsonProperty("userName")
    private Optional<String> userName;
    @JsonProperty("firstName")
    private Optional<String> firstName;
    @JsonProperty("lastName")
    private Optional<String> lastName;
    @JsonProperty("email")
    private String email;
    @JsonProperty("enabled")
    private Optional<Boolean> enabled;
    @JsonProperty("realmRole")
    private Optional<String> realmRole;
    @JsonProperty("group")
    private Optional<String> group;
    @JsonProperty("attributes")
    private Optional<Map<String,String>> attributes;


}
