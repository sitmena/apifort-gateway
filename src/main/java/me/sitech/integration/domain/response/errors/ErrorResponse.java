package me.sitech.integration.domain.response.errors;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ErrorResponse {
    @JsonProperty("code")
    private int code;
    @JsonProperty("errorMessage")
    private String errorMessage;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
