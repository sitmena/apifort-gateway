package me.sitech.integration.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class UserApi extends RouteBuilder {

    private final IntegrationExceptionHandler exception;
    public static final String PATH = "/integration/user";


    public UserApi(IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest(PATH)

                .tag("Integration User")
                .description("Integration User Endpoint")

                /******  Add New Realm ******/
                .post()
                    .description("Add New User")
                    .id("rest-add-user-route-id")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserRequest.class)
                    .to(RoutingConstant.DIRECT_USER_ADD_ROUTE)

                .get("/{realmName}/{userId}")
                    .id("rest-get-user-by-id-route-id")
                    .description("Get User By Id")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_GET_BY_ID_ROUTE)

                .get("/{realmName}/{userName}")
                    .id("rest-get-user-by-user-name-route-id")
                    .description("Get User By UserName")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_GET_BY_USER_NAME_ROUTE)

                .get("/group/{realmName}/{userId}")
                    .id("rest-get-user-id-route-id")
                    .description("Get User Group")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_GET_GROUP_ROUTE)

                .get("/role-effective/{realmName}/{userId}")
                    .id("rest-get-user-role-effective-route-id")
                    .description("Get User Role Effective")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_GET_ROLE_EFFECTIVE_ROUTE)

                .get("/role-available/{realmName}/{userId}")
                    .id("rest-get-user-role-available-route-id")
                    .description("Get User Role Available")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_ROLE_AVAILABLE_ROUTE)

                .get("/{realmName}/{groupName}")
                    .id("rest-get-user-group-name-id")
                    .description("Get Users In Group")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_GET_USERS_IN_GROUP_ROUTE)

                .get("/{realmName}/{roleName}")
                    .id("rest-get-user-role-name-route-id")
                    .description("Get Users In Role")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_USER_GET_ROLE_USERS_ROUTE)

                .post("/assignToGroup")
                    .id("rest-add-user-to-group-route-id")
                    .description("Add User To Group")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserGroupRequest.class)
                    .to(RoutingConstant.DIRECT_USER_ADD_TO_GROUP_ROUTE)

                .post("/assignToRole")
                    .id("rest-add-user-to-group-route-id")
                    .description("Add User To Role")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserRoleRequest.class)
                    .to(RoutingConstant.DIRECT_USER_ADD_TO_ROLE_ROUTE)

                .post("/")
                    .id("rest-update-user-route-id")
                    .description("Update User")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserRequest.class)
                    .to(RoutingConstant.DIRECT_USER_UPDATE_ROUTE)

                .post("/updatePassword")
                    .id("rest-update-user-password-route-id")
                    .description("Update User Password")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserPasswordRequest.class)
                    .to(RoutingConstant.DIRECT_USER_UPDATE_PASSWORD_ROUTE)

                .post("/resetPassword")
                    .id("rest-reset-user-password-route-id")
                    .description("Reset User Password")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserPasswordRequest.class)
                    .to(RoutingConstant.DIRECT_USER_RESET_PASSWORD_ROUTE)

                .post("/killSession")
                    .id("rest-kill-user-session-route-id")
                    .description("Kill User Session")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.UserSessionRequest.class)
                    .to(RoutingConstant.DIRECT_USER_KILL_SESSION_ROUTE)

                .post("/verification")
                    .id("rest-send-user-verification-link-route-id")
                    .description("Send To User Verification Link ")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(me.sitech.integration.domain.request.VerificationLinkRequest.class)
                    .to(RoutingConstant.DIRECT_USER_SEND_VERIFICATION_LINK_ROUTE)

        ;
    }
}
