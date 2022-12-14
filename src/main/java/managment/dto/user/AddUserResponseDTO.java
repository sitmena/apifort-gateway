package managment.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AddUserResponseDTO {


    private String id;
    private long createdTimestamp;
    private String userName;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private String group;
    private Map<String,String> attributes;

}
