package me.sitech.apifort.api.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.gateway.GatewayRouter;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.portal.domain.request.ClientEndpointRequest;
import me.sitech.apifort.api.v1.portal.domain.request.ClientProfileRequest;
import me.sitech.apifort.api.v1.portal.domain.response.ClientEndpointResponse;
import me.sitech.apifort.api.v1.portal.domain.response.ClientProfileResponse;
import me.sitech.apifort.api.v1.portal.domain.response.DefaultResponse;
import me.sitech.apifort.api.v1.portal.domain.response.ErrorResponse;
import me.sitech.apifort.api.v1.portal.rest.client_endpoint.DeleteClientEndpointRouter;
import me.sitech.apifort.api.v1.portal.rest.client_endpoint.PostClientEndpointRouter;
import me.sitech.apifort.api.v1.portal.rest.client_profile.DeleteClientProfileRoute;
import me.sitech.apifort.api.v1.portal.rest.client_profile.GetClientProfileRoute;
import me.sitech.apifort.api.v1.portal.rest.client_profile.PostClientProfileRoute;
import me.sitech.apifort.api.v1.portal.rest.health_check.LiveRoute;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ApiFortAdmin extends RouteBuilder {


    @Inject
    private ExceptionProcessor processor;

    @Override
    public void configure() throws Exception {

        restConfiguration()
                .enableCORS(true)
                .apiContextPath("/api-doc")
                .bindingMode(RestBindingMode.json)
                .dataFormatProperty("prettyPrint", "true")
                .apiProperty("api.title", "APIFort portal Rest Service")
                .apiProperty("api.version", "1.0")
                ;

        onException(Exception.class).handled(true).process(processor).marshal().json();


        rest("/live").tag("Health Check")
                .description("Health Check REST service").consumes("application/json").produces("application/json")
                .securityDefinitions()
                    .bearerToken("authorization", "pass the generated token")
                    .apiKey("api-key").withHeader("api-key").end()
                .end()
                .get().description("Get health-check Status")
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .id("rest-camel-health-id")
                .outType(DefaultResponse.class)
                .bindingMode(RestBindingMode.auto)
                .to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);


        rest("/admin-api/v1/profile")
                .description("API Fort Client Profile CRUD").consumes("application/json").produces("application/json")
                .securityDefinitions()
                .bearerToken("authorization", "pass the generated token")
                .apiKey("api-key").withHeader("api-key").end()
                .end()
                .post().description("POST Client Profile")
                .responseMessage().code(400).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .type(ClientProfileRequest.class).description("Client Profile Request Body")
                .outType(ClientProfileResponse.class).id("postClientProfile").description("post client profile response body")
                .to(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)

                .get().description("GET Client Profile")
                .responseMessage().code(400).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .outType(ClientProfileResponse.class).id("getClientProfile").description("get client profile response body")
                .to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_ROUTE)


                .delete().description("Delete Client Profile")
                .responseMessage().code(400).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .outType(DefaultResponse.class).id("deleteClientProfile").description("Delete client profile response body")
                .to(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);


        rest("/admin-api/v1/endpoints")
                .description("API Fort Client endpoints").consumes("application/json").produces("application/json")
                .post().description("Client endpoint Request Body")
                .securityDefinitions()
                    .bearerToken("authorization", "pass the generated token")
                    .apiKey("api-key").withHeader("api-key").end()
                .end()
                .responseMessage().code(400).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .type(ClientEndpointRequest.class)
                .outType(ClientEndpointResponse.class).id("postClientEndpoint").description("post client endpoint response body")
                .to(GatewayRouter.POST_DIRECT_API_GATEWAY_ROUTE).to(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE);

        rest("/admin-api/v1/endpoints/{uuid}")
                .description("API Fort delete client endpoint by uuid").consumes("application/json").produces("application/json")
                .securityDefinitions()
                    .bearerToken("authorization", "pass the generated token")
                    .apiKey("api-key").withHeader("api-key").end()
                    .end()
                .delete()
                .responseMessage().code(400).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .outType(DefaultResponse.class).id("deleteClientEndpoint").description("delete client endpoint response body")
                .to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);
    }
}
