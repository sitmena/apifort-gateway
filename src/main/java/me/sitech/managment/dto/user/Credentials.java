package me.sitech.managment.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Credentials {

    @JsonProperty("password")
    private String password;

    @JsonProperty("temporary")
    private Boolean temporary;
}
