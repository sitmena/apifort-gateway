package me.sitech.integration.domain.request;

public class VerificationLinkRequest {
    private String realmName;
    private String userId;

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
