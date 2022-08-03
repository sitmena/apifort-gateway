package me.sitech.apifort.api.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.gateway.GatewayRouter;
import me.sitech.apifort.api.v1.portal.rest.client_endpoint.DeleteClientEndpointRouter;
import me.sitech.apifort.api.v1.portal.rest.client_endpoint.PostClientEndpointRouter;
import me.sitech.apifort.api.v1.portal.rest.client_profile.DeleteClientProfileRoute;
import me.sitech.apifort.api.v1.portal.rest.client_profile.GetClientProfileRoute;
import me.sitech.apifort.api.v1.portal.rest.client_profile.PostClientProfileRoute;
import me.sitech.apifort.api.v1.portal.rest.health_check.LiveRoute;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ApiFort extends RouteBuilder {


    @Inject
    private ExceptionProcessor processor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(processor).marshal().json();

        rest("/live").enableCORS(true).get().to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);


        rest("/admin-api/v1/profile")
                .enableCORS(true)
                .post().to(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)
                .get().to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_ROUTE)
                .delete().to(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);


        rest("/admin-api/v1/endpoints")
                .enableCORS(true)
                .post().to(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE);

        rest("/admin-api/v1/endpoints/{uuid}")
                .enableCORS(true)
                .delete().to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);

        rest("/api/*")
                .enableCORS(true)
                .get().to(GatewayRouter.GET_DIRECT_API_GATEWAY_ROUTE)
                .post().to(GatewayRouter.POST_DIRECT_API_GATEWAY_ROUTE)
                .put().to(GatewayRouter.PUT_DIRECT_API_GATEWAY_ROUTE)
                .delete().to(GatewayRouter.DELETE_DIRECT_API_GATEWAY_ROUTE);
    }
}
