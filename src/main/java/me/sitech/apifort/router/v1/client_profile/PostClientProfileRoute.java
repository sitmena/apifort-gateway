package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.request.PostClientProfileRequest;
import me.sitech.apifort.processor.ClientProfileProcessor;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@Slf4j
@ApplicationScoped
public class PostClientProfileRoute extends RouteBuilder {

    public static final String DIRECT_POST_CLIENT_PROFILE_ROUTE = "direct:post-client-profile-route";
    public static final String DIRECT_POST_CLIENT_PROFILE_ROUTE_ID = "post-client-profile-route-id";
    private static final String POST_JSON_VALIDATOR = "json-validator:json/post-profile-validator.json";

    @Inject
    private ClientProfileProcessor processor;

    @Inject
    private ExceptionHandlerProcessor exception;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_POST_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_POST_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_JSON_VALIDATOR)
                .log("${body}").unmarshal().json(PostClientProfileRequest.class)
                .process(processor).marshal().json();
    }
}