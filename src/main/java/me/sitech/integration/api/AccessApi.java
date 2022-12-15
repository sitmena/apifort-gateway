package me.sitech.integration.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import me.sitech.integration.domain.constant.RoutingConstant;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class AccessApi extends RouteBuilder {


    private final IntegrationExceptionHandler exception;
    public static final String PATH = "/integration/access";

    public AccessApi(IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest(PATH.concat("/{realmName}"))
                .tag("Integration Access")
                .description("Integration Access Endpoint")
                .get("/publicKey")
                    .id("rest-get-realm-public-key-route-id")
                    .description("Get Profile Public Key")
                    .to(RoutingConstant.DIRECT_ACCESS_GET_KEY_ROUTE)

                .get("/certificate")
                    .id("rest-get-realm-certificate-route-id")
                    .description("Get Profile certificate")
                    .to(RoutingConstant.DIRECT_ACCESS_GET_CERTIFICATE_ROUTE);
    }
}
