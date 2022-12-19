package me.sitech.apifort.constant;

import me.sitech.apifort.exceptions.APIFortGeneralException;

public final class ApiFortMediaType {

    private ApiFortMediaType() {
        throw new APIFortGeneralException("Utility class");
    }
    public static final String APPLICATION_JSON = "application/json";
    public static final String APPLICATION_URLENCODED = "application/x-www-form-urlencoded";
    public static final String APPLICATION_POST = "POST";
    public static final String APPLICATION_GET = "GET";
    public static final String APPLICATION_PUT = "PUT";
    public static final String APPLICATION_DELETE = "DELETE";
    public static final String APPLICATION_PATCH = "PATCH";
}
