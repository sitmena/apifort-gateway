package me.sitech.integration.route;


import com.google.protobuf.Empty;
import io.quarkus.grpc.GrpcClient;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.domain.module.Dto;
import me.sitech.integration.domain.module.realm.*;
import me.sitech.integration.domain.request.RealmGroupRequest;
import me.sitech.integration.domain.request.RealmRequest;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import me.sitech.integration.mapper.RealmMapper;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ProfileRoute extends RouteBuilder {

    @GrpcClient
    RealmServiceGrpc.RealmServiceBlockingStub realmService;

    private final IntegrationExceptionHandler exception;
    private static final String POST_REALM_ADD_JSON_VALIDATOR = "json-validator:json/integration-realm-post-validator.json";
    private static final String POST_REALM_ADD_GROUP_JSON_VALIDATOR = "json-validator:json/integration-realm-post-add-group-validator.json";
    private static final String LOG_REQUEST_PATTERN = "sent[headers]: ${headers}, sent[body]: ${body}";
    private static final String LOG_RESPONSE_PATTERN = "Received ${body}";

    public ProfileRoute(IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(RoutingConstant.DIRECT_REALM_GET_PROFILE_BY_NAME_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_GET_PROFILE_BY_NAME_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                            RealmResponse kcResponse = realmService.getRealmByName(RealmNameRequest.newBuilder().setRealmName(realmName).build());

                            Dto.RealmDto realmDto = kcResponse.getRealmDto();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toDto(realmDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_GET_PROFILES_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_GET_PROFILES_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            RealmsResponse kcResponse = realmService.getRealms(Empty.newBuilder().build());
                            List<Dto.RealmDto> realmDto = kcResponse.getRealmDtoList();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toDtoList(realmDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_ADD_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_ADD_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_REALM_ADD_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .unmarshal().json(RealmRequest.class)
                .process(exchange -> {
                            RealmRequest realmRequest = exchange.getIn().getBody(RealmRequest.class);
                            RealmResponse kcResponse =
                                    realmService.addRealm(
                                            AddRealmRequest.newBuilder()
                                                    .setRealmName(realmRequest.getRealmName())
                                                    .setDisplayName(realmRequest.getDisplayName())
                                                    .build());
                            Dto.RealmDto realmDto = kcResponse.getRealmDto();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toDto(realmDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_ADD_GROUP_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_ADD_GROUP_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_REALM_ADD_GROUP_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .unmarshal().json(RealmGroupRequest.class)
                .process(exchange -> {
                            RealmGroupRequest request = exchange.getIn().getBody(RealmGroupRequest.class);
                            AddRealmGroupResponse kcResponse =
                                    realmService.addRealmGroup(AddRealmGroupRequest.newBuilder()
                                            .setRealmName(request.getRealmName()).setGroupName(request.getGroupName()).build());
                            exchange.getIn().setBody(kcResponse.getStatus());
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_GET_GROUP_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_GET_GROUP_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                            GetRealmGroupsResponse kcResponse =
                                    realmService.getRealmGroups(RealmNameRequest.newBuilder().setRealmName(realmName).build());
                            List<Dto.GroupDto> groupDto = kcResponse.getGroupDtoList();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toGroupDtoList(groupDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_GET_USER_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_GET_USER_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                            RealmUserResponse kcResponse =
                                    realmService.getRealmUsers(RealmNameRequest.newBuilder().setRealmName(realmName).build());
                            List<Dto.UserDto> userDto = kcResponse.getUserDtoList();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toUserDtoList(userDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_LOGOUT_USERS_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_LOGOUT_USERS_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                            StatusResponse kcResponse = realmService.logoutAllUsers(RealmNameRequest.newBuilder().setRealmName(realmName).build());
                            long status = kcResponse.getStatus();
                            exchange.getIn().setBody(status);
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_GET_CLIENT_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_GET_CLIENT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                            GetRealmClientsResponse kcResponse = realmService.getRealmClients(RealmNameRequest.newBuilder().setRealmName(realmName).build());
                            List<Dto.ClientDto> clientDto = kcResponse.getClientDtoList();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toClientDto(clientDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /***************************************************************************/
        from(RoutingConstant.DIRECT_REALM_GET_ROLE_ROUTE)
                .id(RoutingConstant.DIRECT_REALM_GET_ROLE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG, LOG_REQUEST_PATTERN)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                            GetRealmRolesResponse kcResponse = realmService.getRealmRoles(RealmNameRequest.newBuilder().setRealmName(realmName).build());
                            List<Dto.RoleDto> roleDto = kcResponse.getRoleDtoList();
                            exchange.getIn().setBody(RealmMapper.INSTANCE.toRoleDto(roleDto));
                        }
                ).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
//        /***************************************************************************/


    }
}
