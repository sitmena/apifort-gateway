package me.sitech.integration.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class TokenApi extends RouteBuilder {

    private final IntegrationExceptionHandler exception;
    public static final String PATH = "/integration/token";

    public TokenApi(IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest(PATH)
                .tag("Integration Token")
                .description("Integration Access Endpoint")

                .get("/userCredentials/{realmName}/{clientId}/{clientSecret}/{userName}/{userPassword}")
                    .id("rest-login-by-user-credentials-route-id")
                    .description("User Login Credentials")
                    .produces(ApiFortMediaType.APPLICATION_JSON)
                    .to(RoutingConstant.DIRECT_TOKEN_LOGIN_BY_USER_CREDENTIALS_ROUTE)


                .get("/serviceCredentials/{realmName}/{clientId}/{clientSecret}")
                .id("rest-login-by-service-route-id")
                .description("Service Login Credentials")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .to(RoutingConstant.DIRECT_LOGIN_BY_SERVICE_CREDENTIALS_ROUTE)
                ;
    }

    }
