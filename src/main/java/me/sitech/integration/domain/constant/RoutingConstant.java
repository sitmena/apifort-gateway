package me.sitech.integration.domain.constant;

public class RoutingConstant {


    private RoutingConstant() {
        throw new IllegalStateException("Utility class");
    }

    /***************************** ACCESS *********************************/
    public static final String DIRECT_ACCESS_GET_KEY_ROUTE = "direct:get-realm-key-route";
    public static final String DIRECT_ACCESS_GET_KEY_ROUTE_ID = "direct-get-realm-key-route-id";
    public static final String DIRECT_ACCESS_GET_CERTIFICATE_ROUTE = "direct:get-realm-certificate-route";
    public static final String DIRECT_ACCESS_GET_CERTIFICATE_ROUTE_ID = "direct-get-realm-certificate-route-id";
    /***************************** PROFILE *********************************/
    public static final String DIRECT_REALM_GET_PROFILE_BY_NAME_ROUTE = "direct:get-realm-by-name-route";
    public static final String DIRECT_REALM_GET_PROFILE_BY_NAME_ROUTE_ID = "direct-get-realm-by-name-route-id";
    public static final String DIRECT_REALM_GET_PROFILES_ROUTE = "direct:get-realms-route";
    public static final String DIRECT_REALM_GET_PROFILES_ROUTE_ID = "direct-get-realms-route-id";
    public static final String DIRECT_REALM_ADD_ROUTE = "direct:add-realm-route";
    public static final String DIRECT_REALM_ADD_ROUTE_ID = "direct-add-realm-route-id";
    public static final String DIRECT_REALM_ADD_GROUP_ROUTE = "direct:add-realm-group-route";
    public static final String DIRECT_REALM_ADD_GROUP_ROUTE_ID = "direct-add-realm-group-route-id";
    public static final String DIRECT_REALM_GET_GROUP_ROUTE = "direct:get-realm-group-route";
    public static final String DIRECT_REALM_GET_GROUP_ROUTE_ID = "direct-get-realm-group-route-id";
    public static final String DIRECT_REALM_GET_USER_ROUTE = "direct:get-realm-user-route";
    public static final String DIRECT_REALM_GET_USER_ROUTE_ID = "direct-get-realm-user-route-id";
    public static final String DIRECT_REALM_LOGOUT_USERS_ROUTE = "direct:lgo-out-realm-users-route";
    public static final String DIRECT_REALM_LOGOUT_USERS_ROUTE_ID = "direct-lgo-out-realm-users-route-id";
    public static final String DIRECT_REALM_GET_CLIENT_ROUTE = "direct:get-realm-clients-route";
    public static final String DIRECT_REALM_GET_CLIENT_ROUTE_ID = "direct-get-realm-clients-route-id";
    public static final String DIRECT_REALM_GET_ROLE_ROUTE = "direct:get-realm-roles-route";
    public static final String DIRECT_REALM_GET_ROLE_ROUTE_ID = "direct-get-realm-roles-route-id";
    public static final String DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE = "direct:get-login-by-service-credentials-route";
    public static final String DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE_ID = "direct:get-login-by-service-credentials-route-id";

    public static final String DIRECT_REFRESH_TOKEN_ROUTE = "direct:get-refresh-token-route";
    public static final String DIRECT_REFRESH_TOKEN_ROUTE_ID = "direct-get-refresh-token-route-route-id";
    /******************************************************************************************************************/
    public static final String DIRECT_USER_ADD_TO_GROUP_ROUTE = "direct:user-add-to-group-route";
    public static final String DIRECT_USER_ADD_TO_GROUP_ROUTE_ID = "direct-user-add-to-group-route-id";
    public static final String DIRECT_USER_ADD_TO_ROLE_ROUTE = "direct:user-add-to-role-route";
    public static final String DIRECT_USER_ADD_TO_ROLE_ROUTE_ID = "direct-user-add-to-role-route-id";
    public static final String DIRECT_USER_UPDATE_ROUTE = "direct:user-update-route";
    public static final String DIRECT_USER_UPDATE_ROUTE_ID = "direct-user-update-route-id";
    public static final String DIRECT_USER_UPDATE_PASSWORD_ROUTE = "direct:user-update-password-route";
    public static final String DIRECT_USER_UPDATE_PASSWORD_ROUTE_ID = "direct-user-update-password-route-id";
    public static final String DIRECT_USER_RESET_PASSWORD_ROUTE = "direct:user-reset-password-route";
    public static final String DIRECT_USER_RESET_PASSWORD_ROUTE_ID = "direct-user-reset-password-route-id";
    public static final String DIRECT_USER_KILL_SESSION_ROUTE = "direct:user-kill-session-route";
    public static final String DIRECT_USER_KILL_SESSION_ROUTE_ID = "direct-user-kill-session-route-id";
    public static final String DIRECT_USER_SEND_VERIFICATION_LINK_ROUTE = "direct:user-send-verification-route";

    public static final String DIRECT_USER_GET_BY_ROLES_GROUP_ROUTE = "direct:user-get-by-roles-group-route";
    public static final String DIRECT_USER_GET_BY_ROLES_GROUP_ROUTE_ID = "direct:user-get-by-roles-group-route-id";


    public static final String DIRECT_USER_REMOVE_GROUP_ROUTE = "direct:user-remove-group-route";
    public static final String DIRECT_USER_REMOVE_GROUP_ROUTE_ID = "direct:user-remove-group-route_id";
    public static final String DIRECT_USER_REMOVE_ROLE_ROUTE="direct:user-remove-role-route";
    public static final String DIRECT_USER_REMOVE_ROLE_ROUTE_ID="direct:user-remove-role-route_id";

    public static final String DIRECT_GET_ALL_USER_IN_REALM_ROUTE = "direct:user-get-all-in-realm-route";
    public static final String DIRECT_GET_ALL_USER_IN_REALM_ROUTE_ID = "direct:user-get-all-in-realm-route";
    public static final String DIRECT_USER_SEND_VERIFICATION_LINK_ROUTE_ID = "direct-user-send-verification-route-id";
    public static final String DIRECT_USER_GET_BY_ID_ROUTE = "direct:get-user-by-id-route";
    public static final String DIRECT_USER_GET_BY_ID_ROUTE_ID = "direct-get-user-by-id-route-id";
    public static final String DIRECT_USER_GET_BY_USER_NAME_ROUTE = "direct:get-user-by-user-name-route";
    public static final String DIRECT_USER_GET_BY_USER_NAME_ROUTE_ID = "direct-get-user-by-user-name-route-id";
    public static final String DIRECT_USER_ADD_ROUTE = "direct:add-user-route";
    public static final String DIRECT_USER_ADD_ROUTE_ID = "direct-add-user-route-id";
    public static final String  DIRECT_USER_GET_USERS_IN_GROUP_ROUTE = "direct:get-users-in-group-route";
    public static final String  DIRECT_USER_GET_USERS_IN_GROUP_ROUTE_ID = "direct-get-users-in-group-route-id";
    public static final String DIRECT_USER_GET_GROUP_ROUTE = "direct:user-get-group-route";
    public static final String DIRECT_USER_GET_GROUP_ROUTE_ID = "direct-user-get-group-route-id";
    public static final String DIRECT_USER_GET_ROLE_EFFECTIVE_ROUTE = "direct:user-send-effective-route";
    public static final String DIRECT_USER_GET_ROLE_EFFECTIVE_ROUTE_ID = "direct-user-send-effective-route-id";
    public static final String DIRECT_USER_ROLE_AVAILABLE_ROUTE = "direct:user-role-available-route";
    public static final String DIRECT_USER_ROLE_AVAILABLE_ROUTE_ID = "direct-user-role-available-route-id";
    public static final String DIRECT_USER_GET_GROUP_USERS_ROUTE = "direct:user-get-user-groups-route";
    public static final String DIRECT_USER_GET_GROUP_USERS_ROUTE_ID = "direct-user-get-user-groups-route-id";
    public static final String DIRECT_USER_GET_ROLE_USERS_ROUTE = "direct:user-get-role-users-route";
    public static final String DIRECT_USER_GET_ROLE_USERS_ROUTE_ID = "direct-user-get-role-users-route-id";
    public static final String DIRECT_TOKEN_LOGIN_BY_USER_CREDENTIALS_ROUTE = "direct:user-get-login-by-user-credentials-route";
    public static final String DIRECT_TOKEN_LOGIN_BY_USER_CREDENTIALS_ROUTE_ID = "direct-user-get-login-by-user-credentials-route-id";
    public static final String CAMEL_HEADER_REALM_NAME_KEY ="realmName";
    public static final String CAMEL_HEADER_USER_FROM_KEY ="from";
    public static final String CAMEL_HEADER_USER_SIZE_KEY ="size";
    public static final String CAMEL_HEADER_USER_ID_KEY ="userId";
    public static final String CAMEL_HEADER_CLIENT_ID_KEY ="clientId";
    public static final String CAMEL_HEADER_GROUP_NAME_KEY ="groupName";
    public static final String CAMEL_HEADER_ROLE_NAME_KEY ="roleName";
    public static final String CAMEL_HEADER_ROLES_KEY ="roles";
    public static final String CAMEL_HEADER_GROUPS_KEY ="groups";
    public static final String CAMEL_HEADER_PAGE_NUMBER_KEY = "pageNumber";
    public static final String  CAMEL_HEADER_PAGE_SIZE_KEY = "pageSize";

    public static final String CAMEL_HEADER_USER_ROLES_KEY = "roleName";
    public static final String CAMEL_HEADER_CLIENT_SECRET_KEY ="clientSecret";
    public static final String CAMEL_HEADER_USER_NAME_KEY ="userName";
    public static final String CAMEL_HEADER_USER_PASSWORD_KEY ="userPassword";

}
