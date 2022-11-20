package managment.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Getter
@Setter
public class AddUserRequestDTO {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("firstName")
    private Optional<String> firstName;
    @JsonProperty("lastName")
    private Optional<String> lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("email")
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String realmName;
    @JsonProperty("realmRole")
    private Optional<String> realmRole;
    @JsonProperty("group")
    private Optional<String> group;
    @JsonProperty("attributes")
    private Optional<Map<String, String>> attributes;
    @JsonProperty("credentials")
    private Credentials credentials;



}
