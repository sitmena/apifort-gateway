package me.sitech.apifort.api.v1.gateway;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import me.sitech.apifort.api.v1.security.JwtAuthenticationRoute;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

@ApplicationScoped
public class GatewayRoute extends RouteBuilder {

    private static final String API_GATEWAY_ROUT_ID = "get-gateway-rout-id";

    @Inject
    private ExceptionProcessor exceptionProcessor;

    @Inject
    private GatewayProcessor gatewayProcessor;

    @Override
    public void configure() throws Exception {


        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();


        fromF("rest:%s:api//*", "GET")
            .routeId(API_GATEWAY_ROUT_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .process(gatewayProcessor)
            .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));


        fromF("rest:%s:api//*", "POST")
                .routeId(API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        fromF("rest:%s:api//*", "DELETE")
                .routeId(API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

        fromF("rest:%s:api//*", "PUT")
                .routeId(API_GATEWAY_ROUT_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(gatewayProcessor)
                .toD(String.format("http://%s?bridgeEndpoint=true", "${headers.dss-endpoint}"));

    }
}
