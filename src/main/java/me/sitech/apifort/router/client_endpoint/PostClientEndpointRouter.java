package me.sitech.apifort.router.client_endpoint;

import me.sitech.apifort.domain.request.ClientEndpointRequest;
import me.sitech.apifort.processor.CreateEndpointProcessor;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import me.sitech.apifort.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PostClientEndpointRouter extends RouteBuilder {
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE = "direct:post-client-endpoint-route";
    private static final String POST_CLIENT_ENDPOINT_ROUTE_ID = "post-client-endpoint-route-id";

    @Inject
    private CreateEndpointProcessor processor;

    @Inject
    private ExceptionProcessor exception;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_POST_CLIENT_ENDPOINT_ROUTE)
            .routeId(POST_CLIENT_ENDPOINT_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .unmarshal()
            .json(JsonLibrary.Jackson, ClientEndpointRequest.class)
            .process(processor)
            .marshal().json();
    }
}
