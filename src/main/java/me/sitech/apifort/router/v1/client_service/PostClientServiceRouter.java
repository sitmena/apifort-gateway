package me.sitech.apifort.router.v1.client_service;

import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.domain.request.PostClientServiceReq;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.processor.PostServiceProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class PostClientServiceRouter extends RouteBuilder {

    public static final String DIRECT_POST_CLIENT_SERVICE_ROUTE = "direct:post-client-service-route";
    public static final String DIRECT_POST_CLIENT_SERVICE_ROUTE_ID = "post-client-service-route-id";

    public static final String DIRECT_PUT_CLIENT_SERVICE_ROUTE = "direct:put-client-service-route";
    public static final String DIRECT_PUT_CLIENT_SERVICE_ROUTE_ID = "put-client-service-route-id";

    private static final String POST_JSON_VALIDATOR = "json-validator:json/post-service-validator.json";
    private static final String PUT_JSON_VALIDATOR = "json-validator:json/put-service-validator.json";


    private final PostServiceProcessor processor;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public PostClientServiceRouter(PostServiceProcessor processor,ExceptionHandlerProcessor exception) {
        this.processor = processor;
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_POST_CLIENT_SERVICE_ROUTE)
                .id(DIRECT_POST_CLIENT_SERVICE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_JSON_VALIDATOR)
                .log("${body}").unmarshal().json(PostClientServiceReq.class)
                .setHeader(ApiFort.API_FORT_ROUTER_ACTION,constant(ApiFort.API_FORT_CREATE_ACTION))
                .process(processor)
                .marshal().json();


        from(DIRECT_PUT_CLIENT_SERVICE_ROUTE)
                .id(DIRECT_PUT_CLIENT_SERVICE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(PUT_JSON_VALIDATOR)
                .log("${body}").unmarshal().json(PostClientServiceReq.class)
                .setHeader(ApiFort.API_FORT_ROUTER_ACTION,constant(ApiFort.API_FORT_UPDATE_ACTION))
                .process(processor)
                .marshal().json();
    }
}
