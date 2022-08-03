package me.sitech.apifort.api.v1.portal.rest.client_endpoint;

import me.sitech.apifort.api.v1.portal.domain.request.ClientEndpointRequest;
import me.sitech.apifort.api.v1.portal.processor.ClientEndpointProcessor;
import me.sitech.apifort.security.JwtAuthenticationRoute;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PostClientEndpointRouter extends RouteBuilder {
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE = "direct:post-client-endpoint-route";
    private static final String POST_CLIENT_ENDPOINT_ROUTE_ID = "post-client-endpoint-route-id";

    @Inject
    private ClientEndpointProcessor clientEndpointProcessor;

    @Inject
    private ExceptionProcessor exceptionProcessor;


    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();

        from(DIRECT_POST_CLIENT_ENDPOINT_ROUTE)
                .routeId(POST_CLIENT_ENDPOINT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .unmarshal()
                .json(JsonLibrary.Jackson, ClientEndpointRequest.class)
                .process(clientEndpointProcessor)
                .marshal().json();
    }
}
