package me.sitech.apifort.router.v1.gateway;

import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.router.v1.gateway.processor.GatewayExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.gateway.processor.GatewayProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static me.sitech.apifort.constant.ApiFort.APIFORT_DOWNSTREAM_SERVICE_HEADER;
import static me.sitech.apifort.constant.ApiFortMediaType.*;

@ApplicationScoped
public class GatewayRouter extends RouteBuilder {

    public static final String GET_DIRECT_SECURE_API_GATEWAY_ROUTE = "direct:get-secure-gateway-route-id";
    public static final String GET_DIRECT_SECURE_API_GATEWAY_ROUTE_ID = "get-secure-gateway-route-id";

    public static final String POST_DIRECT_SECURE_API_GATEWAY_ROUTE = "direct:post-secure-gateway-route-id";
    public static final String POST_DIRECT_SECURE_API_GATEWAY_ROUTE_ID = "post-secure-gateway-route-id";

    public static final String DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE = "direct:delete-secure-gateway-route-id";
    public static final String DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE_ID = "delete-secure-gateway-route-id";

    public static final String PUT_DIRECT_SECURE_API_GATEWAY_ROUTE = "direct:put-secure-gateway-route-id";
    public static final String PUT_DIRECT_SECURE_API_GATEWAY_ROUTE_ID = "put-secure-gateway-route-id";

    public static final String PATCH_DIRECT_SECURE_API_GATEWAY_ROUTE = "direct:patch-secure-gateway-route-id";
    public static final String PATCH_DIRECT_SECURE_API_GATEWAY_ROUTE_ID = "patch-secure-gateway-route-id";


    public static final String GET_DIRECT_GUEST_API_GATEWAY_ROUTE = "direct:get-guest-gateway-route-id";
    public static final String GET_DIRECT_GUEST_API_GATEWAY_ROUTE_ID = "get-guest-gateway-route-id";

    public static final String POST_DIRECT_GUEST_API_GATEWAY_ROUTE = "direct:post-guest-gateway-route-id";
    public static final String POST_DIRECT_GUEST_API_GATEWAY_ROUTE_ID = "post-guest-gateway-route-id";

    private static final String DOWNSTREAM_ENDPOINT_HEADER = String.format("${headers.%s}", APIFORT_DOWNSTREAM_SERVICE_HEADER);
    private static final String CAMEL_BRIDGE_ROUTING_PATH = "http://%s?bridgeEndpoint=true&throwExceptionOnFailure=false&okStatusCodeRange=100-599";


    private final GatewayProcessor processor;
    private final GatewayExceptionHandlerProcessor exception;

    @Inject
    public GatewayRouter(GatewayProcessor processor, GatewayExceptionHandlerProcessor exception) {
        this.processor = processor;
        this.exception = exception;
    }

    @Override
    public void configure() {

        onException(Exception.class).handled(true).process(exception);

        from(GET_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .routeId(GET_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log(LoggingLevel.INFO,DOWNSTREAM_ENDPOINT_HEADER)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_GET))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);

        from(POST_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .routeId(POST_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log(LoggingLevel.INFO,DOWNSTREAM_ENDPOINT_HEADER)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_POST))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);

        from(DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .routeId(DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log(LoggingLevel.INFO,DOWNSTREAM_ENDPOINT_HEADER)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_DELETE))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);

        from(PUT_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .routeId(PUT_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log(LoggingLevel.INFO,DOWNSTREAM_ENDPOINT_HEADER)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_PUT))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);

        from(PATCH_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .routeId(PATCH_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log(LoggingLevel.INFO,DOWNSTREAM_ENDPOINT_HEADER)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_PATCH))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);

        //PUBLIC ENDPOINTS
        from(GET_DIRECT_GUEST_API_GATEWAY_ROUTE)
                .routeId(GET_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .process(processor)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_GET))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);

        from(POST_DIRECT_GUEST_API_GATEWAY_ROUTE)
                .routeId(POST_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .process(processor)
                .setHeader(Exchange.HTTP_METHOD, constant(APPLICATION_POST))
                .toD(String.format(CAMEL_BRIDGE_ROUTING_PATH, DOWNSTREAM_ENDPOINT_HEADER))
                .removeHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER)
                .removeHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION)
                .removeHeader(ApiFort.API_KEY_HEADER);
    }
}
