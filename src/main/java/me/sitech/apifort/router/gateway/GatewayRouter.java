package me.sitech.apifort.router.gateway;

import me.sitech.apifort.processor.GatewayProcessor;
import me.sitech.apifort.router.security.JwtAuthenticationRoute;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GatewayRouter extends RouteBuilder {

    public static final String GET_DIRECT_API_GATEWAY_ROUTE = "direct:get-gateway-route-id";
    private static final String GET_DIRECT_API_GATEWAY_ROUTE_ID = "get-gateway-route-id";

    public static final String POST_DIRECT_API_GATEWAY_ROUTE = "direct:post-gateway-route-id";
    private static final String POST_DIRECT_API_GATEWAY_ROUTE_ID = "post-gateway-route-id";

    public static final String DELETE_DIRECT_API_GATEWAY_ROUTE = "direct:delete-gateway-route-id";
    private static final String DELETE_DIRECT_API_GATEWAY_ROUTE_ID = "delete-gateway-route-id";

    public static final String PUT_DIRECT_API_GATEWAY_ROUTE = "direct:put-gateway-route-id";
    private static final String PUT_DIRECT_API_GATEWAY_ROUTE_ID = "put-gateway-route-id";


    @Inject
    private ExceptionProcessor exception;

    @Inject
    private GatewayProcessor processor;

    @Override
    public void configure() throws Exception {

        //Exception Handler
        onException(Exception.class).handled(true).process(exception).marshal().json();

        //TODO: Customize CORS headers.
        restConfiguration().enableCORS(true);

        from(GET_DIRECT_API_GATEWAY_ROUTE)
                .routeId(GET_DIRECT_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log("${headers.dss-endpoint}")
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        from(POST_DIRECT_API_GATEWAY_ROUTE)
                .routeId(POST_DIRECT_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log("${headers.dss-endpoint}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        from(DELETE_DIRECT_API_GATEWAY_ROUTE)
                .routeId(DELETE_DIRECT_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log("${headers.dss-endpoint}")
                .setHeader(Exchange.HTTP_METHOD, constant("DELETE"))
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        from(PUT_DIRECT_API_GATEWAY_ROUTE)
                .routeId(PUT_DIRECT_API_GATEWAY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(processor)
                .log("${headers.dss-endpoint}")
                .setHeader(Exchange.HTTP_METHOD, constant("PUT"))
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));
    }
}
