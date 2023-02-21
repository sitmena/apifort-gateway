package me.sitech.integration.route;


import io.quarkus.grpc.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.domain.module.users.*;
import me.sitech.integration.domain.request.KillUserSessionRequest;
import me.sitech.integration.domain.request.UserPasswordRequest;
import me.sitech.integration.domain.request.UserRequest;
import me.sitech.integration.domain.request.VerificationLinkRequest;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import me.sitech.integration.mapper.GroupMapper;
import me.sitech.integration.mapper.RoleMapper;
import me.sitech.integration.mapper.UserMapper;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.HashMap;

@Slf4j
@ApplicationScoped
public class UserRoute extends RouteBuilder {
    @GrpcClient
    UserServiceGrpc.UserServiceBlockingStub userService;
    private final IntegrationExceptionHandler exception;
    private static final String POST_USER_ADD_JSON_VALIDATOR = "json-validator:json/integration-user-post-add-validator.json";
    private static final String POST_USER_ASSIGN_TO_GROUP_JSON_VALIDATOR = "json-validator:json/integration-user-post-group-validator.json";
    private static final String POST_USER_ASSIGN_TO_ROLE_JSON_VALIDATOR = "json-validator:json/integration-user-post-role-validator.json";
    private static final String POST_USER_UPDATE_JSON_VALIDATOR = "json-validator:json/integration-user-post-update-validator.json";
    private static final String POST_USER_UPDATE_PASSWORD_JSON_VALIDATOR = "json-validator:json/integration-user-post-update-password-validator.json";
    private static final String POST_USER_RESET_PASSWORD_JSON_VALIDATOR = "json-validator:json/integration-user-post-reset-password-validator.json";
    private static final String POST_USER_KILL_SESSION_JSON_VALIDATOR = "json-validator:json/integration-user-post-kill-session-validator.json";
    private static final String POST_USER_SEND_VERIFICATION_LINK_JSON_VALIDATOR = "json-validator:json/integration-user-post-send-verification-link-validator.json";
    private static final String LOG_REQUEST_PATTERN = "sent[headers]: ${headers}, sent[body]: ${body}";
    private static final String LOG_RESPONSE_PATTERN = "Received ${body}";

    public UserRoute( IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(RoutingConstant.DIRECT_USER_ADD_ROUTE )
                .id(RoutingConstant.DIRECT_USER_ADD_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_ADD_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(UserRequest.class)
                .process(exchange -> {
                            UserRequest request = exchange.getIn().getBody(UserRequest.class);
                            UserResponse kcResponse =
                                    userService.addUser(
                                            AddUserRequest.newBuilder()
                                                    .setUserName(request.getUserName())
                                                    .setFirstName(request.getFirstName() == null || request.getFirstName().isEmpty() ? "" : request.getFirstName())
                                                    .setLastName(request.getLastName() == null || request.getLastName().isEmpty() ? "" : request.getLastName())
                                                    .setEmail(request.getEmail())
                                                    .setRealmName(request.getRealmName())
                                                    .setRole(request.getRole() == null || request.getRole().isEmpty() ? "" : request.getRole())
                                                    .setGroup(request.getGroup() == null || request.getGroup().isEmpty() ? "" : request.getGroup())
                                                    .putAllAttributes(request.getAttributes() == null || request.getAttributes().isEmpty() ? new HashMap<>() : request.getAttributes())
                                                    .setCredentials(Credentials.newBuilder().setPassword(request.getCredentials().getPassword()).setTemporary(request.getCredentials().getTemporary()).build())
                                                    .build());
                            exchange.getIn().setBody(UserMapper.INSTANCE.toDto(kcResponse.getUserDto()));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/

        from(RoutingConstant.DIRECT_USER_GET_BY_ID_ROUTE)
                .id(RoutingConstant.DIRECT_USER_GET_BY_ID_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String userId = exchange.getIn().getHeader("userId", String.class);
                    UserResponse kcResponse =
                            userService.getUserById(GetUserByIdRequest.newBuilder()
                                    .setRealmName(realmName)
                                    .setUserId(userId).build());
                    exchange.getIn().setBody(UserMapper.INSTANCE.toDto(kcResponse.getUserDto()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_GET_BY_USER_NAME_ROUTE)
                .id(RoutingConstant.DIRECT_USER_GET_BY_USER_NAME_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String userName = exchange.getIn().getHeader("userName", String.class);
                    UserResponse kcResponse =
                            userService.getUserByUserName(
                                    GetUserByUserNameRequest.newBuilder()
                                            .setRealmName(realmName)
                                            .setUserName(userName)
                                            .build());
                    exchange.getIn().setBody(UserMapper.INSTANCE.toDto(kcResponse.getUserDto()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_GET_GROUP_ROUTE)
                .id(RoutingConstant.DIRECT_USER_GET_GROUP_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String userId = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_USER_ID_KEY, String.class);
                    GetUserGroupsResponse KcResponse =
                            userService.getUserGroups(
                                    GetUserGroupRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());
                    exchange.getIn().setBody(GroupMapper.INSTANCE.toDtoList(KcResponse.getGroupDtoList()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_GET_ROLE_EFFECTIVE_ROUTE)
                .id(RoutingConstant.DIRECT_USER_GET_ROLE_EFFECTIVE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String userId = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_USER_ID_KEY, String.class);
                    GetUserRoleResponse KcResponse =
                            userService.getUserRoleEffective(
                                    UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());
                    exchange.getIn().setBody(RoleMapper.INSTANCE.toDtoList(KcResponse.getRoleDtoList()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_ROLE_AVAILABLE_ROUTE)
                .id(RoutingConstant.DIRECT_USER_ROLE_AVAILABLE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String userId = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_USER_ID_KEY, String.class);
                    GetUserRoleResponse KcResponse =
                            userService.getUserRoleAvailable(
                                    UserRoleRequest.newBuilder().setRealmName(realmName).setUserId(userId).build());
                    exchange.getIn().setBody(RoleMapper.INSTANCE.toDtoList(KcResponse.getRoleDtoList()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_GET_USERS_IN_GROUP_ROUTE)
                .id(RoutingConstant.DIRECT_USER_GET_USERS_IN_GROUP_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String groupName = exchange.getIn().getHeader("groupName", String.class);
                    UsersResponse kcResponse = userService.findAllUsersInGroup(
                            UserGroupRequest.newBuilder().setRealmName(realmName).setGroupName(groupName).build());
                    exchange.getIn().setBody(UserMapper.INSTANCE.toDtoList(kcResponse.getUserDtoList()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_GET_ROLE_USERS_ROUTE)
                .id(RoutingConstant.DIRECT_USER_GET_ROLE_USERS_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String roleName = exchange.getIn().getHeader("roleName", String.class);
                    UsersResponse KcResponse = userService.findUserByRole(
                            FindUserRoleRequest.newBuilder().setRealmName(realmName).setRoleName(roleName).build());
                    exchange.getIn().setBody(UserMapper.INSTANCE.toDtoList(KcResponse.getUserDtoList()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_ADD_TO_GROUP_ROUTE)
                .id(RoutingConstant.DIRECT_USER_ADD_TO_GROUP_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_ASSIGN_TO_GROUP_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(me.sitech.integration.domain.request.UserGroupRequest.class)
                .process(exchange -> {
                            me.sitech.integration.domain.request.UserGroupRequest request = exchange.getIn().getBody(me.sitech.integration.domain.request.UserGroupRequest.class);
                            StatusReplay kcResponse =
                                    userService.addUserToGroup(
                                            AddUserGroupRequest.newBuilder()
                                                    .setRealmName(request.getRealmName())
                                                    .setUserName(request.getUserName())
                                                    .setGroupName(request.getGroupName())
                                                    .build());
                            String status = kcResponse.getStatusCode();
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_ADD_TO_ROLE_ROUTE)
                .id(RoutingConstant.DIRECT_USER_ADD_TO_ROLE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_ASSIGN_TO_ROLE_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(me.sitech.integration.domain.request.UserRoleRequest.class)
                .process(exchange -> {
                            me.sitech.integration.domain.request.UserRoleRequest request = exchange.getIn().getBody(me.sitech.integration.domain.request.UserRoleRequest.class);
                            StatusReplay kcResponse =
                                    userService.addUserToGroup(
                                            AddUserGroupRequest.newBuilder()
                                                    .setRealmName(request.getRealmName())
                                                    .setUserName(request.getUserName())
                                                    .setGroupName(request.getRoleName())
                                                    .build());
                            String status = kcResponse.getStatusCode() ;
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_UPDATE_ROUTE) // DIRECT_USER_POST_UPDATE_ROUTE
                .id(RoutingConstant.DIRECT_USER_UPDATE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_UPDATE_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(UserRequest.class)
                .process(exchange -> {
                            UserRequest request = exchange.getIn().getBody(UserRequest.class);

                    UserResponse kcResponse =
                            userService.updateUser(UpdateUserRequest.newBuilder()
                                    .setRealmName(request.getRealmName())
                                    .setUserName(request.getUserName().isEmpty() ? "" : request.getUserName())
                                    .setFirstName(request.getFirstName() == null || request.getFirstName().isEmpty() ? "" : request.getFirstName())
                                    .setLastName(request.getLastName() == null || request.getLastName().isEmpty() ? "" : request.getLastName())
                                    .setEmail(request.getEmail().isEmpty() ? "" : request.getEmail())
//                                    .setEnabled(request.getEnabled().isEmpty() ? userAttributes.isEnabled() : request.getEnabled().get())
                                    .setRole(request.getRole() == null || request.getRole().isEmpty() ? "" : request.getRole())
                                    .setGroup(request.getGroup() == null || request.getGroup().isEmpty() ? "" : request.getGroup())
                                    .putAllAttributes(request.getAttributes() == null || request.getAttributes().isEmpty() ? new HashMap<>() : request.getAttributes())
                                    .build());
                            exchange.getIn().setBody(UserMapper.INSTANCE.toDto(kcResponse.getUserDto()));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();

        /****************************************************************************/

        from(RoutingConstant.DIRECT_USER_UPDATE_PASSWORD_ROUTE)
                .id(RoutingConstant.DIRECT_USER_UPDATE_PASSWORD_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_UPDATE_PASSWORD_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(UserPasswordRequest.class)
                .process(exchange -> {
                            UserPasswordRequest request = exchange.getIn().getBody(UserPasswordRequest.class);
                    StatusReplay kcResponse =
                            userService.updateUserPassword(UpdateUserPasswordRequest.newBuilder()
                                    .setRealmName(request.getRealmName())
                                    .setUserId(request.getUserId())
                                    .setPassword(request.getNewPassword())
                                    .build());
                            String status = kcResponse.getStatusCode() ;
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();

        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_RESET_PASSWORD_ROUTE)
                .id(RoutingConstant.DIRECT_USER_RESET_PASSWORD_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_RESET_PASSWORD_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(UserPasswordRequest.class)
                .process(exchange -> {
                            UserPasswordRequest request = exchange.getIn().getBody(UserPasswordRequest.class);
                    StatusReplay KcResponse =
                            userService.resetUserPassword(ResetUserPasswordRequest.newBuilder()
                                    .setRealmName(request.getRealmName())
                                    .setUserId(request.getUserId())
                                    .setUserName(request.getUserName())
                                    .setOldPassword(request.getOldPassword())
                                    .setNewPassword(request.getNewPassword())
                                    .build());

                    String status = KcResponse.getStatusCode() ;
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_KILL_SESSION_ROUTE)
                .id(RoutingConstant.DIRECT_USER_KILL_SESSION_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_KILL_SESSION_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(KillUserSessionRequest.class)
                .process(exchange -> {
                    KillUserSessionRequest request = exchange.getIn().getBody(KillUserSessionRequest.class);
                    UserStatusResponse KcResponse =
                            userService.killUserSession(DeleteUserSessionRequest.newBuilder()
                                    .setRealmName(request.getRealmName())
                                    .setSessionState(request.getSessionState())
                                    .build());
                            long status = KcResponse.getStatus() ;
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_USER_SEND_VERIFICATION_LINK_ROUTE)
                .id(RoutingConstant.DIRECT_USER_SEND_VERIFICATION_LINK_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_USER_SEND_VERIFICATION_LINK_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(VerificationLinkRequest.class)
                .process(exchange -> {
                    VerificationLinkRequest request = exchange.getIn().getBody(VerificationLinkRequest.class);
                    StatusReplay KcResponse =
                            userService.sendVerificationLink(SendVerificationLinkRequest.newBuilder().setRealmName(request.getRealmName())
                                    .setUserId(request.getUserId()).build());
                            String status = KcResponse.getStatusCode() ;
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/


    }
}
