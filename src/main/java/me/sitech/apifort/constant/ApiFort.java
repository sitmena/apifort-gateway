package me.sitech.apifort.constant;

import java.util.Arrays;
import java.util.List;

public final class ApiFort {

    public static final String CAMEL_HTTP_PATH_HEADER = "CamelHttpPath";
    public static final String CAMEL_HTTP_METHOD_HEADER = "CamelHttpMethod";
    public static final String REST_ACCESS_CONTROL_ALLOWED_HEADERS = "Origin, Accept, X-Requested-With, Content-Type,Access-Control-Request-Method, Access-Control-Request-Headers,Authorization,x-api-key";
    public static final String REST_ACCESS_CONTROL_ALLOWED_ORIGIN = "*";
    public static final String API_KEY_HEADER = "x-api-key";
    public static final String API_TOKEN_ROLES = "x-token-roles";

    public static final String API_FORT_JWT_TOKEN_PREFIX = "Bearer ";
    public static final String API_FORT_EMPTY_STRING = "";

    public static final List<String> allowedPublicMethods = Arrays.asList("POST","GET");
    public static final List<String> allowedPrivateMethods = Arrays.asList("POST","GET","PUT","DELETE");

}
