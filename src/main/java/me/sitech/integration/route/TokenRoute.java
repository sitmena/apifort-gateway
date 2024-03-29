package me.sitech.integration.route;

import io.quarkus.grpc.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.domain.module.tokens.*;
import me.sitech.integration.domain.request.RefreshTokenRestRequest;
import me.sitech.integration.domain.request.ServiceLoginCredentialsRequest;
import me.sitech.integration.domain.request.UserLoginCredentialsRequest;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import me.sitech.integration.mapper.TokenMapper;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class TokenRoute extends RouteBuilder {

    private static final String POST_USER_LOGIN_JSON_VALIDATOR = "json-validator:json/integration-token-post-user-login-credentials-validator.json";
    private static final String POST_SERVICE_LOGIN_JSON_VALIDATOR = "json-validator:json/integration-token-post-service-login-credentials-validator.json";
    private static final String POST_REFRESH_TOKEN_JSON_VALIDATOR = "json-validator:json/integration-refresh-token-validator.json";
    @GrpcClient
    TokenServiceGrpc.TokenServiceBlockingStub tokensService;

    private final IntegrationExceptionHandler exception;

    private static final String LOG_RESPONSE_PATTERN = "Received ${body}";
    private static final String LOG_REQUEST_PATTERN = "sent[headers]: ${headers}, sent[body]: ${body}";

    public TokenRoute(IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(RoutingConstant.DIRECT_TOKEN_LOGIN_BY_USER_CREDENTIALS_ROUTE)
                .id(RoutingConstant.DIRECT_TOKEN_LOGIN_BY_USER_CREDENTIALS_ROUTE_ID)
                .to(POST_USER_LOGIN_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .unmarshal().json(UserLoginCredentialsRequest.class)
                .process(exchange -> {
                    UserLoginCredentialsRequest request = exchange.getIn().getBody(UserLoginCredentialsRequest.class);
                    UserAccessTokenResponse token = tokensService.loginByUserCredentials(
                            LoginByUserCredentialsRequest.newBuilder().setRealmName(request.getRealmName()).setClientId(request.getClientId())
                                    .setClientSecret(request.getClientSecret()).setUserName(request.getUserName()).setUserPassword(request.getUserPassword())
                                    .build());
                    exchange.getIn().setBody(TokenMapper.INSTANCE.toDto(token.getUserAccessTokenDto()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();

        /****************************************************************************/
        from(RoutingConstant.DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE)
                .id(RoutingConstant.DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE_ID)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .to(POST_SERVICE_LOGIN_JSON_VALIDATOR)
                .unmarshal().json(ServiceLoginCredentialsRequest.class)
                .process(exchange -> {
                    ServiceLoginCredentialsRequest request = exchange.getIn().getBody(ServiceLoginCredentialsRequest.class);
                    UserAccessTokenResponse token = tokensService.loginByServiceCredentials(
                            LoginByServiceCredentialsRequest.newBuilder().setRealmName(request.getRealmName())
                            .setClientId(request.getClientId()).setClientSecret(request.getClientSecret()).build());
                    exchange.getIn().setBody(TokenMapper.INSTANCE.toDto(token.getUserAccessTokenDto()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
        /****************************************************************************/
        from(RoutingConstant.DIRECT_REFRESH_TOKEN_ROUTE)
                .id(RoutingConstant.DIRECT_REFRESH_TOKEN_ROUTE_ID)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .to(POST_REFRESH_TOKEN_JSON_VALIDATOR)
                .unmarshal().json(RefreshTokenRestRequest.class)
                .process(exchange -> {
                    RefreshTokenRestRequest request = exchange.getIn().getBody(RefreshTokenRestRequest.class);
                    UserAccessTokenResponse token = tokensService.refreshToken(
                            RefreshTokenRequest.newBuilder()
                                    .setRealmName(request.getRealmName())
                                    .setClientId(request.getClientId())
                                    .setClientSecret(request.getClientSecret())
                                    .setRefreshedToken(request.getRefreshedToken())
                                    .build());
                    exchange.getIn().setBody(TokenMapper.INSTANCE.toDto(token.getUserAccessTokenDto()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();
    }
}
