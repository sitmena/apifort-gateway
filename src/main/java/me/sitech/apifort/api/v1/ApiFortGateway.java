package me.sitech.apifort.api.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.router.gateway.GatewayRouter;
import me.sitech.apifort.router.client_endpoint.DeleteClientEndpointRouter;
import me.sitech.apifort.router.client_endpoint.PostClientEndpointRouter;
import me.sitech.apifort.router.client_profile.DeleteClientProfileRoute;
import me.sitech.apifort.router.client_profile.GetClientProfileRoute;
import me.sitech.apifort.router.client_profile.PostClientProfileRoute;
import me.sitech.apifort.router.health_check.LiveRoute;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ApiFortGateway extends RouteBuilder {


    @Inject
    private ExceptionProcessor processor;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin","*")
                .corsHeaderProperty("Access-Control-Allow-Headers","Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization, x-api-key");


        onException(Exception.class).handled(true).process(processor).marshal().json();

        rest("/live")
                .get()
                .to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);


        rest("/admin-api/v1/profile")

                .post()
                    .to(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)
                .get()
                    .to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_ROUTE)
                .delete("/{client_profile_uuid}")
                    .to(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);


        rest("/admin-api/v1/endpoints")
                .post()
                .to(GatewayRouter.POST_DIRECT_API_GATEWAY_ROUTE).to(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE);

        rest("/admin-api/v1/endpoints/{uuid}")
                .delete()
                    .to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);

        rest("/api/*")
                .get()
                    .to(GatewayRouter.GET_DIRECT_API_GATEWAY_ROUTE)
                .post()
                    .to(GatewayRouter.POST_DIRECT_API_GATEWAY_ROUTE)
                .put()
                    .to(GatewayRouter.PUT_DIRECT_API_GATEWAY_ROUTE)
                .delete()
                    .to(GatewayRouter.DELETE_DIRECT_API_GATEWAY_ROUTE);
    }
}
