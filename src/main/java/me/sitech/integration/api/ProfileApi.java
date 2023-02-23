package me.sitech.integration.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.domain.request.RealmGroupRequest;
import me.sitech.integration.domain.request.RealmRequest;
import me.sitech.integration.domain.response.ProfileUserResponse;
import me.sitech.integration.domain.response.realm.RealmClientResponse;
import me.sitech.integration.domain.response.realm.RealmGroupResponse;
import me.sitech.integration.domain.response.realm.RealmResponse;
import me.sitech.integration.domain.response.realm.RealmRoleResponse;
import me.sitech.integration.domain.response.user.LogoutAllUsersResponse;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class ProfileApi extends RouteBuilder {

    private final IntegrationExceptionHandler exception;

    public ProfileApi(IntegrationExceptionHandler exception) {
         this.exception = exception;
    }

    public static final String PATH = "/integration/profile";


    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest(PATH)
                .tag("Integration Profile")
                .description("Integration Profile Endpoint")

                /******  Get Realm List ******/
                .get()
                    .id("rest-get-realms-route-id")
                    .description("Profile rest service")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .consumes(ApiFortMediaType.APPLICATION_JSON)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(RealmResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_GET_PROFILES_ROUTE)

                /******  Get Realm By Name ******/
                .get("/{realmName}")
                    .id("rest-get-realm-by-name-route-id")
                    .description("Get Profile By Name")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_REALM_GET_PROFILE_BY_NAME_ROUTE)

                /******  Add New Realm ******/
                .post()
                    .id("rest-add-realm-route-id")
                    .description("Add New Realm")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(RealmRequest.class)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(RealmRequest.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_ADD_ROUTE)

                /****************** Group ********************/
                /****** Add Group To Realm *******/
                .post("/group")
                    .id("rest-add-realm-group-route-id")
                    .description("Add Group To Realm")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .type(RealmGroupRequest.class)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(RealmGroupResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_ADD_GROUP_ROUTE)

                /****** Get Realm Groups *******/
                .get("/{realmName}/group")
                    .id("rest-get-realm-groups-route-id")
                    .description("Get Realm Groups")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(RealmGroupResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_GET_GROUP_ROUTE)


                /****************** Users ********************/
                /****** Get Realm Users *******/
                .get("/{realmName}/user")
                    .id("rest-get-realm-users-route-id")
                    .description("Get Realm Users")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(ProfileUserResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_GET_USER_ROUTE)

                /****** Logout All Realm Users *******/
                .delete("/{realmName}/user")
                    .id("rest-logout-realm-users-route-id")
                    .description("Logout All Realm Users")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .outType(LogoutAllUsersResponse.class)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(LogoutAllUsersResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_LOGOUT_USERS_ROUTE)


                /****************** Clients ********************/
                /****** Get Realm Clients *******/
                .get("/{realmName}/client")
                    .id("rest-get-realm-clients-route-id")
                    .description("Get Realm Clients")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(RealmClientResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_GET_CLIENT_ROUTE)


                /****************** Roles ********************/
                /****** Get Realm Roles *******/
                .get("/{realmName}/role")
                    .id("rest-get-realm-roles-route-id")
                    .description("Get Realm Roles")
                    .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                    .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                    .responseMessage().code(ApiFortStatusCode.OK).responseModel(RealmRoleResponse.class).endResponseMessage()
                    .to(RoutingConstant.DIRECT_REALM_GET_ROLE_ROUTE)
        ;
    }
}
