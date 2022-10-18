package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.processor.ClientProfileUpdateProcessor;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@Slf4j
@ApplicationScoped
public class PutClientProfileRoute extends RouteBuilder {

    public static final String DIRECT_PUT_CLIENT_PROFILE_ROUTE = "direct:put-client-profile-route";
    private static final String PUT_CLIENT_PROFILE_ROUTE_ID = "put-client-profile-route-id";

    @Inject
    private ClientProfileUpdateProcessor processor;

    @Inject
    private ExceptionHandlerProcessor exception;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_PUT_CLIENT_PROFILE_ROUTE)
                .routeId(PUT_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .unmarshal().json(PostClientProfileReq.class)
                .process(processor)
                .marshal().json();
    }
}