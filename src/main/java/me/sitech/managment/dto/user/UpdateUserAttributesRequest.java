package me.sitech.managment.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserAttributesRequest {

    private String realmName;
    private String userId;
    private Map<String,String> attributes;
}
