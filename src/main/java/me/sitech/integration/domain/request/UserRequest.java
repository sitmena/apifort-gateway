package me.sitech.integration.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import java.util.Optional;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserRequest {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userId")
    private String userId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("userName")
    private String userName;
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("email")
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String realmName;
    @JsonProperty("role")
    private String role;
    @JsonProperty("group")
    private String group;
    @JsonProperty("attributes")
    private Map<String, String> attributes;
    @JsonProperty("credentials")
    private CredentialsRequest credentials;
    @JsonProperty("enabled")
    private  String enabled;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
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

    public CredentialsRequest getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsRequest credentials) {
        this.credentials = credentials;
    }
}
