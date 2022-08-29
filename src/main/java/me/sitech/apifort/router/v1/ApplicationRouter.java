package me.sitech.apifort.router.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortIds;
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
import me.sitech.apifort.router.v1.redis_cache.RedisCacheRouter;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ApplicationRouter extends RouteBuilder {

    @Inject
    private ExceptionProcessor processor;

    @ConfigProperty(name = "apifort.admin.allowed-headers")
    public String allowedHeaders;

    @ConfigProperty(name = "apifort.admin.allowed-origin")
    public String allowedOrigin;

    @ConfigProperty(name = "apifort.admin.enableCORS")
    public Boolean enableCORS;

    @Override
    public void configure() throws Exception {

        // use jetty for rest service
        restConfiguration()
                .enableCORS(true)
                .corsHeaderProperty("Access-Control-Allow-Origin", allowedOrigin)
                .corsHeaderProperty("Access-Control-Allow-Headers", allowedHeaders)
                .port("{{quarkus.http.port}}")
                //.contextPath("/v1")
                .bindingMode(RestBindingMode.json)
                .apiContextPath("api-doc")
                .apiProperty("api.title", "APIFort portal Rest Service")
                .apiProperty("api.version", "1.0")
                .enableCORS(enableCORS);

        onException(Exception.class).handled(true).process(processor).marshal().json();

        rest("/live")
                .tag("APIFort Health")
                .description("APIFort Health Endpoint")
                .get()
                .id(ApiFortIds.REST_GET_HEALTH_ROUTE_ID)
                .description("Health Check REST service")
                .produces("application/json")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
                .to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);

        rest("/admin-api/profile")
                .description("APIFort Profile Endpoint(s)")
                .tag("APIFort Profiles")
                .post()
                .id(ApiFortIds.REST_POST_CLIENT_PROFILE_ROUTE_ID)
                .description("Post ApiFort Profile")
                .consumes("application/json").produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientProfileResponse.class).endResponseMessage()
                .type(ClientProfileRequest.class)
                .outType(ClientProfileResponse.class)
                .to(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)


                .get("/{realm}")
                .id(ApiFortIds.REST_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID)
                .description("Get ApiFort Profile by realm")
                .produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.NO_CONTENT).message("No Content").endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientProfileDetailsResponse.class).endResponseMessage()
                .outType(ClientProfileDetailsResponse.class)
                .to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)

                .delete("/{client_profile_uuid}")
                .id(ApiFortIds.REST_DELETE_CLIENT_PROFILE_ROUTE)
                .description("Delete ApiFort Profile")
                .produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.BAD_REQUEST_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).message("Success").responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
                .to(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);

        rest("/admin-api/endpoints")
                .description("ApiFort user define Endpoints")
                .tag("APIFort Profile Endpoints")


                .get("/{client_profile_uuid}")
                .id(ApiFortIds.REST_GET_CLIENT_ENDPOINT_ROUTE_ID)
                .description("ApiFort GET user defined endpoint by using profile uuid")
                .produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientEndpointResponse.class).endResponseMessage()
                .outType(List.class)
                .to(GetClientEndpointRouter.DIRECT_GET_CLIENT_ENDPOINT_ROUTE)

                .post()
                .id(ApiFortIds.REST_POST_CLIENT_ENDPOINT_ROUTE_ID)
                .description("ApiFort POST user defined endpoint")
                .consumes("application/json").produces("application/json")
                .responseMessage().code(StatusCode.BAD_REQUEST).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(ClientEndpointResponse.class).endResponseMessage()
                .type(PostEndpointRequest.class)
                .outType(ClientEndpointResponse.class)
                .to(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE)

                .delete("/{uuid}")
                .id(ApiFortIds.REST_DELETE_CLIENT_ENDPOINT_ROUTER_ID)
                .description("ApiFort DELETE user defined endpoint")
                .consumes("application/json")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)
                .to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);

        rest("/apis/*")
                .description("APIFort Secure Gateway Entry Points")
                .tag("APIFort Gateway (Private)")

                .get()
                .id(ApiFortIds.REST_GET_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private GET Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.GET_DIRECT_SECURE_API_GATEWAY_ROUTE)

                .post()
                .id(ApiFortIds.REST_POST_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private POST Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.POST_DIRECT_SECURE_API_GATEWAY_ROUTE)


                .delete()
                .id(ApiFortIds.REST_DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private Delete Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE)

                .put()
                .id(ApiFortIds.REST_PUT_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private PUT Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.PUT_DIRECT_SECURE_API_GATEWAY_ROUTE);


        rest("/api/*")
                .description("APIFort Public Gateway Entry Points")
                .tag("APIFort Gateway (Public)")

                .get()
                .id(ApiFortIds.REST_GET_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .description("Public GET Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.GET_DIRECT_GUEST_API_GATEWAY_ROUTE)

                .post()
                .id(ApiFortIds.REST_POST_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .description("Public POST Gateway")
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.POST_DIRECT_GUEST_API_GATEWAY_ROUTE);


        rest("/admin-api/cache/")
                .description("APIFort Cache Endpoints")
                .tag("APIFort Cache")

                .delete("/{cache_key}")
                .id(ApiFortIds.REST_DELETE_ITEM_CACHE_ROUTE_ID)
                .description("Delete cache data by key")
                .to(RedisCacheRouter.DIRECT_DELETE_ITEM_CACHE_ROUTE)
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).message(StatusCode.OK_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)

                .delete("/{cache_key}/{cache_value}")
                .id(ApiFortIds.REST_DELETE_LIST_CACHE_ROUTE_ID)
                .description("Delete cache data from list using key and value")
                .to(RedisCacheRouter.DIRECT_DELETE_LIST_CACHE_ROUTE)
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).message(StatusCode.OK_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class)

                .post("/{cache_realm}")
                .id(ApiFortIds.REST_SYNC_CACHE_ROUTE_ID)
                .description("Sync Realm data")
                .to(RedisCacheRouter.DIRECT_SYNC_CACHE_ROUTE)
                .responseMessage().code(StatusCode.UNAUTHORIZED).message(StatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .responseMessage().code(StatusCode.OK).message(StatusCode.OK_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .outType(DefaultResponse.class);
    }
}
