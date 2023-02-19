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
    private Optional<String> firstName;
    @JsonProperty("lastName")
    private Optional<String> lastName;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("email")
    private String email;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String realmName;
    @JsonProperty("role")
    private Optional<String> role;
    @JsonProperty("group")
    private Optional<String> group;
    @JsonProperty("attributes")
    private Optional<Map<String, String>> attributes;
    @JsonProperty("credentials")
    private CredentialsRequest credentials;
    @JsonProperty("enabled")
    private  String enabled;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Optional<String> getFirstName() {
        return firstName;
    }

    public void setFirstName(Optional<String> firstName) {
        this.firstName = firstName;
    }

    public Optional<String> getLastName() {
        return lastName;
    }

    public void setLastName(Optional<String> lastName) {
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

    public Optional<String> getRole() {
        return role;
    }

    public void setRole(Optional<String> role) {
        this.role = role;
    }

    public String getEnabled() {
        return enabled;
    }

    public void setEnabled(String enabled) {
        this.enabled = enabled;
    }

    public Optional<String> getGroup() {
        return group;
    }

    public void setGroup(Optional<String> group) {
        this.group = group;
    }

    public Optional<Map<String, String>> getAttributes() {
        return attributes;
    }

    public void setAttributes(Optional<Map<String, String>> attributes) {
        this.attributes = attributes;
    }

    public CredentialsRequest getCredentials() {
        return credentials;
    }

    public void setCredentials(CredentialsRequest credentials) {
        this.credentials = credentials;
    }
}
