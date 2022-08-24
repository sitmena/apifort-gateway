package me.sitech.apifort.router.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.domain.request.ClientProfileRequest;
import me.sitech.apifort.domain.request.PostEndpointRequest;
import me.sitech.apifort.domain.response.common.DefaultResponse;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointResponse;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsResponse;
import me.sitech.apifort.domain.response.profile.ClientProfileResponse;
import me.sitech.apifort.processor.ExceptionProcessor;
import me.sitech.apifort.router.v1.client_endpoint.DeleteClientEndpointRouter;
import me.sitech.apifort.router.v1.client_endpoint.GetClientEndpointRouter;
import me.sitech.apifort.router.v1.client_endpoint.PostClientEndpointRouter;
import me.sitech.apifort.router.v1.client_profile.DeleteClientProfileRoute;
import me.sitech.apifort.router.v1.client_profile.GetClientProfileRoute;
import me.sitech.apifort.router.v1.client_profile.PostClientProfileRoute;
import me.sitech.apifort.router.v1.gateway.GatewayRouter;
import me.sitech.apifort.router.v1.health_check.LiveRoute;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

import static me.sitech.apifort.constant.ApiFort.REST_ACCESS_CONTROL_ALLOWED_HEADERS;
import static me.sitech.apifort.constant.ApiFort.REST_ACCESS_CONTROL_ALLOWED_ORIGIN;

@Slf4j
@ApplicationScoped
public class ApplicationRouter extends RouteBuilder {

    @Inject
    private ExceptionProcessor processor;

    @Override
    public void configure() throws Exception {

        // use jetty for rest service
        restConfiguration()
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin", REST_ACCESS_CONTROL_ALLOWED_ORIGIN)
                .corsHeaderProperty("Access-Control-Allow-Headers", REST_ACCESS_CONTROL_ALLOWED_HEADERS)
                .port("{{quarkus.http.port}}")
                //.contextPath("/v1")
                .bindingMode(RestBindingMode.json)
                .apiContextPath("api-doc")
                .apiProperty("api.title", "APIFort portal Rest Service")
                .apiProperty("api.version", "1.0")
                .enableCORS(true);

        onException(Exception.class).handled(true).process(processor).marshal().json();

        rest("/live")
                .tag("Health Check")
                .description("APIFort Health Endpoint")
            .get()
                .id("rest-rout-live-id")
                .description("Health Check REST service")
                .produces("application/json")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
            .to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);

        rest("/admin-api/profile")
                .description("APIFort Profile Endpoint(s)")
                .tag("Define User Endpoint")
           .post()
                .id(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)
                .description("Post ApiFort Profile")
                .consumes("application/json").produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientProfileResponse.class).endResponseMessage()
                .type(ClientProfileRequest.class)
                .outType(ClientProfileResponse.class)
                .to(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)


           .get("/{realm}")
                .id(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)
                .description("Get ApiFort Profile by realm")
                .produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.NO_CONTENT).message("No Content").endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientProfileDetailsResponse.class).endResponseMessage()
                .outType(ClientProfileDetailsResponse.class)
                .to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)

           .delete("/{client_profile_uuid}")
                .id(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE)
                .description("Delete ApiFort Profile")
                .produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).message("Success").responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
                .to(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);

        rest("/admin-api/endpoints")
                .description("ApiFort user define Endpoints")
                .tag("ApiFort User Endpoints")


            .get("/{client_profile_uuid}")
                .id(GetClientEndpointRouter.DIRECT_GET_CLIENT_ENDPOINT_ROUTE)
                .description("ApiFort GET user defined endpoint by using profile uuid")
                .produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientEndpointResponse.class).endResponseMessage()
                .outType(List.class)
                .to(GetClientEndpointRouter.DIRECT_GET_CLIENT_ENDPOINT_ROUTE)

            .post()
                .id(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE)
                .description("ApiFort POST user defined endpoint")
                .consumes("application/json").produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientEndpointResponse.class).endResponseMessage()
                .type(PostEndpointRequest.class)
                .outType(ClientEndpointResponse.class)
                .to(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE)

            .delete("/{uuid}")
                .id(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)
                .description("ApiFort DELETE user defined endpoint")
                .consumes("application/json")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
                .to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);

        rest("/api/*")
                .description("APIFort Gateway Entry Points")
                .tag("Gateway")

            .get()
                .id(GatewayRouter.GET_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .description("ApiFort GET Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.GET_DIRECT_SECURE_API_GATEWAY_ROUTE)

            .post()
                .id(GatewayRouter.POST_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .description("ApiFort POST Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.POST_DIRECT_SECURE_API_GATEWAY_ROUTE)

            .delete("/{uuid}")
                .id(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)
                .description("ApiFort DELETE user defined endpoint")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
                .to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)

            .put()
                .id(GatewayRouter.PUT_DIRECT_SECURE_API_GATEWAY_ROUTE)
                .description("ApiFort PUT Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.PUT_DIRECT_SECURE_API_GATEWAY_ROUTE);


        rest("/guest/*")
                .description("APIFort Gateway Entry Points")
                .tag("Gateway")

            .get()
                .id(GatewayRouter.GET_DIRECT_GUEST_API_GATEWAY_ROUTE)
                .description("ApiFort GET Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.GET_DIRECT_GUEST_API_GATEWAY_ROUTE)

            .post()
                .id(GatewayRouter.POST_DIRECT_GUEST_API_GATEWAY_ROUTE)
                .description("ApiFort POST Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.POST_DIRECT_GUEST_API_GATEWAY_ROUTE);

        /*
           .get()
                .id(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_ROUTE)
                .description("Get ApiFort Profile")
                .consumes("application/json")
                .responseMessage().code(400).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(401).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(200).responseModel(ClientProfileDetailsResponse.class).endResponseMessage()
                .outType(ClientProfileDetailsResponse.class)
                .to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_ROUTE);
        */
    }
}
