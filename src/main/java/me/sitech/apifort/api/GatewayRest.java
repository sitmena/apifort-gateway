package me.sitech.apifort.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.config.ApiFortProps;
import me.sitech.apifort.constant.ApiFortIds;
import me.sitech.apifort.processor.GatewayExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.gateway.GatewayRouter;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class GatewayRest extends RouteBuilder {

    private final ApiFortProps apiFortProps;
    private final GatewayExceptionHandlerProcessor exception;

    @Inject
    public GatewayRest(ApiFortProps apiFortProps,GatewayExceptionHandlerProcessor exception){
        this.apiFortProps = apiFortProps;
        this.exception=exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception);

        restConfiguration()
                .enableCORS(apiFortProps.admin().enableCors())
                .corsHeaderProperty("Access-Control-Allow-Headers", apiFortProps.admin().allowedHeaders())
                .corsHeaderProperty("Access-Control-Allow-Origin", apiFortProps.admin().allowedOrigin())
                .port("{{quarkus.http.port}}")
                //.contextPath("/v1")
                //.bindingMode(RestBindingMode.off)
                .apiContextPath("api-doc")
                .apiProperty("api.title", "APIFort portal Rest Service")
                .apiProperty("api.version", "1.0");

        //APIFORT ROUTER PRIVATE SERVICE(s)
        rest(String.format("/%s/*", apiFortProps.admin().privateContext()))
                .description("APIFort Secure Gateway Entry Points")
                .tag("APIFort Gateway (Private)")
             .get()
                .id(ApiFortIds.REST_GET_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private GET Gateway")
             .to(GatewayRouter.GET_DIRECT_SECURE_API_GATEWAY_ROUTE)


             .post()
                .id(ApiFortIds.REST_POST_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private POST Gateway")
             .to(GatewayRouter.POST_DIRECT_SECURE_API_GATEWAY_ROUTE)

             .delete()
                .id(ApiFortIds.REST_DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private Delete Gateway")
             .to(GatewayRouter.DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE)

             .put()
                .id(ApiFortIds.REST_PUT_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private PUT Gateway")
             .to(GatewayRouter.PUT_DIRECT_SECURE_API_GATEWAY_ROUTE)

             .patch()
                .id(ApiFortIds.REST_PATCH_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private PATCH Gateway")
             .to(GatewayRouter.PATCH_DIRECT_SECURE_API_GATEWAY_ROUTE);


        //APIFORT ROUTER PUBLIC SERVICES
        rest(String.format("/%s/*",apiFortProps.admin().publicContext()))
                .description("APIFort Public Gateway Entry Points")
                .tag("APIFort Gateway (Public)")

                .get()
                .id(ApiFortIds.REST_GET_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .description("Public GET Gateway")
                .to(GatewayRouter.GET_DIRECT_GUEST_API_GATEWAY_ROUTE)

                .post()
                .id(ApiFortIds.REST_POST_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .description("Public POST Gateway")
                .to(GatewayRouter.POST_DIRECT_GUEST_API_GATEWAY_ROUTE);
    }
}
