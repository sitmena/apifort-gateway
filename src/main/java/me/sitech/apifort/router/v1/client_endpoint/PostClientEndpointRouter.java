package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.domain.request.PostEndpointRequest;
import me.sitech.apifort.processor.CreateEndpointProcessor;
import me.sitech.apifort.processor.ExceptionProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PostClientEndpointRouter extends RouteBuilder {
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE = "direct:post-client-endpoint-route";
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE_ID = "post-client-endpoint-route-id";

    @Inject
    private CreateEndpointProcessor processor;

    @Inject
    private ExceptionProcessor exception;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_POST_CLIENT_ENDPOINT_ROUTE)
            .routeId(DIRECT_POST_CLIENT_ENDPOINT_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .log("${body}")
            .unmarshal().json(PostEndpointRequest.class)
            .process(processor).marshal().json();
    }
}
