package me.sitech.apifort.api.v1;

import me.sitech.apifort.api.v1.gateway.GatewayProcessor;
import me.sitech.apifort.api.v1.security.JwtAuthenticationRoute;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class GatewayApiRouter extends RouteBuilder {

    private static final String GET_API_GATEWAY_ROUT_ID     = "get-gateway-rout-id";
    private static final String PUT_API_GATEWAY_ROUT_ID     = "put-gateway-rout-id";
    private static final String POST_API_GATEWAY_ROUT_ID    = "post-gateway-rout-id";
    private static final String DELETE_API_GATEWAY_ROUT_ID  = "delete-gateway-rout-id";

    @Inject
    private ExceptionProcessor exceptionProcessor;

    @Inject
    private GatewayProcessor gatewayProcessor;

    @Override
    public void configure() throws Exception {

        //Exception Handler
        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();

        fromF("rest:%s:api//*", "GET")
                .routeId(GET_API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        fromF("rest:%s:api//*", "PUT")
                .routeId(PUT_API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        fromF("rest:%s:api//*", "POST")
                .routeId(POST_API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        fromF("rest:%s:api//*", "DELETE")
                .routeId(DELETE_API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));



    }
}
