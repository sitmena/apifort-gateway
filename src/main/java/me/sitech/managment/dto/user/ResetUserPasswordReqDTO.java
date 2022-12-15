package me.sitech.managment.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ResetUserPasswordReqDTO {

    private String realmName;
    private String userId;
    private String userName;
    private String oldPassword;
    private String newPassword;
}
