package me.sitech.managment.dto.user;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class updateUserPasswordDTO {

    private String realmName;
    private String userId;
    private String password;
}
