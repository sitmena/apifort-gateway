package me.sitech.integration.route;

import io.quarkus.grpc.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.domain.module.tokens.LoginByServiceCredentialsRequest;
import me.sitech.integration.domain.module.tokens.LoginByUserCredentialsRequest;
import me.sitech.integration.domain.module.tokens.TokenServiceGrpc;
import me.sitech.integration.domain.module.tokens.UserAccessTokenResponse;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import me.sitech.integration.mapper.TokenMapper;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class TokenRoute extends RouteBuilder {

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
//                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.INFO,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String clientId = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_CLIENT_ID_KEY, String.class);
                    String clientSecret = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_CLIENT_SECRET_KEY, String.class);
                    String userName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_USER_NAME_KEY, String.class);
                    String userPassword = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_USER_PASSWORD_KEY, String.class);
                    UserAccessTokenResponse token = tokensService.loginByUserCredentials(
                            LoginByUserCredentialsRequest.newBuilder().setRealmName(realmName).setClientId(clientId)
                                    .setClientSecret(clientSecret).setUserName(userName).setUserPassword(userPassword)
                                    .build());
                    exchange.getIn().setBody(TokenMapper.INSTANCE.toDto(token.getUserAccessTokenDto()));
                }).log(LoggingLevel.INFO,LOG_RESPONSE_PATTERN).marshal().json();

        /****************************************************************************/


        from(RoutingConstant.DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE)
                .id(RoutingConstant.DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE_ID)
//                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,LOG_REQUEST_PATTERN)
                .process(exchange -> {
                    String realmName = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_REALM_NAME_KEY, String.class);
                    String clientId = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_CLIENT_ID_KEY, String.class);
                    String clientSecret = exchange.getIn().getHeader(RoutingConstant.CAMEL_HEADER_CLIENT_SECRET_KEY, String.class);
                    UserAccessTokenResponse token = tokensService.loginByServiceCredentials(
                            LoginByServiceCredentialsRequest.newBuilder().setRealmName(realmName).setClientId(clientId).setClientSecret(clientSecret).build());
                    exchange.getIn().setBody(TokenMapper.INSTANCE.toDto(token.getUserAccessTokenDto()));
                }).log(LoggingLevel.DEBUG,LOG_RESPONSE_PATTERN).marshal().json();

    }
}
