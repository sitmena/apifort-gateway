package me.sitech.integration.domain.response.user;

import me.sitech.integration.domain.response.ProfileUserResponse;

import java.util.Map;

public class UserResponse extends ProfileUserResponse {

    private String role;
    private String group;
    private Map<String, String> attributes;


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
