package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.processor.CreateEndpointProcessor;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PostClientEndpointRouter extends RouteBuilder {
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE = "direct:post-client-endpoint-route";
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE_ID = "post-client-endpoint-route-id";
    private static final String POST_JSON_VALIDATOR = "json-validator:json/post-endpoint-validator.json";

    private final CreateEndpointProcessor processor;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public PostClientEndpointRouter(CreateEndpointProcessor processor,
                                    ExceptionHandlerProcessor exception) {
        this.processor = processor;
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_POST_CLIENT_ENDPOINT_ROUTE)
            .routeId(DIRECT_POST_CLIENT_ENDPOINT_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .to(POST_JSON_VALIDATOR)
            .unmarshal().json(PostEndpointReq.class)
            .log("${body}")
            .process(processor).marshal().json();
    }
}
