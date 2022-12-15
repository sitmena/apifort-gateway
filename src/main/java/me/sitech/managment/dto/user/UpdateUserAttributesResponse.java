package me.sitech.managment.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserAttributesResponse {

    private String id;
    private Long createdTimestamp;
    private String username;
    private boolean enabled;
    private String firstName;
    private String lastName;
    private String email;
    private Map<String,String> attributes;

}
