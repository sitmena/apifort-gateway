package me.sitech.integration.domain.request;

public class RefreshTokenRestRequest extends ServiceLoginCredentialsRequest{

    private  String refreshedToken;

    public String getRefreshedToken() {
        return refreshedToken;
    }

    public void setRefreshedToken(String refreshedToken) {
        this.refreshedToken = refreshedToken;
    }
}
