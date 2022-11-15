package managment.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class updateUserDTO {

    private String realmName;
    private String userId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean enabled;
    private String role;
    private String group;
    private Map<String,String> attributes;


}
