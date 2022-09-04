package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.request.ClientProfileRequest;
import me.sitech.apifort.processor.ClientProfileProcessor;
import me.sitech.apifort.processor.ExceptionProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@Slf4j
@ApplicationScoped
public class PostClientProfileRoute extends RouteBuilder {

    public static final String DIRECT_POST_CLIENT_PROFILE_ROUTE = "direct:post-client-profile-route";
    public static final String DIRECT_POST_CLIENT_PROFILE_ROUTE_ID = "post-client-profile-route-id";

    @Inject
    private ClientProfileProcessor processor;

    @Inject
    private ExceptionProcessor exceptionProcessor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();

        from(DIRECT_POST_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_POST_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log("${body}").unmarshal().json(ClientProfileRequest.class)
                .process(processor).marshal().json();
    }
}