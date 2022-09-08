package me.sitech.apifort.router.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortIds;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.request.ClientProfileRequest;
import me.sitech.apifort.domain.request.PostEndpointRequest;
import me.sitech.apifort.domain.response.common.GeneralResponse;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointResponse;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsResponse;
import me.sitech.apifort.domain.response.profile.ClientProfileResponse;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_endpoint.DeleteClientEndpointRouter;
import me.sitech.apifort.router.v1.client_endpoint.GetClientEndpointRouter;
import me.sitech.apifort.router.v1.client_endpoint.PostClientEndpointRouter;
import me.sitech.apifort.router.v1.client_profile.DeleteClientProfileRoute;
import me.sitech.apifort.router.v1.client_profile.GetClientProfileRoute;
import me.sitech.apifort.router.v1.client_profile.PostClientProfileRoute;
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
public class AdminPortalRest extends RouteBuilder {

    @ConfigProperty(name = "apifort.admin.allowed-headers")
    public String allowedHeaders;

    @ConfigProperty(name = "apifort.admin.allowed-origin")
    public String allowedOrigin;

    @ConfigProperty(name = "apifort.admin.enableCORS")
    public Boolean enableCORS;

    @Inject
    private ExceptionHandlerProcessor exception;

    @Override
    public void configure() throws Exception {



        // use jetty for rest service
        restConfiguration()
                .corsHeaderProperty("Access-Control-Allow-Origin", allowedOrigin)
                .corsHeaderProperty("Access-Control-Allow-Headers", allowedHeaders)
                .port("{{quarkus.http.port}}")
                //.contextPath("/v1")
                //.bindingMode(RestBindingMode.off)
                .apiContextPath("api-doc")
                .apiProperty("api.title", "APIFort portal Rest Service")
                .apiProperty("api.version", "1.0")
                .enableCORS(enableCORS);

        onException(Exception.class).handled(true).process(exception).marshal().json();

        //HEALTH CHECK SERVICE
        rest("/live")
                .tag("APIFort Health")
                .description("APIFort Health Endpoint")
             .get()
                .id(ApiFortIds.REST_GET_HEALTH_ROUTE_ID)
                .description("Health Check REST service")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(GeneralResponse.class).endResponseMessage()
                .outType(GeneralResponse.class)
            .to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);

        //PROFILE REST SERVICE(s)
        rest("/admin-api/profile")
                .description("APIFort Profile Endpoint(s)")
                .tag("APIFort Profiles")
            .post()
                .id(ApiFortIds.REST_POST_CLIENT_PROFILE_ROUTE_ID)
                .description("Post ApiFort Profile")
                .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientProfileResponse.class).endResponseMessage()
                .type(ClientProfileRequest.class)
                .outType(ClientProfileResponse.class)
            .to(PostClientProfileRoute.DIRECT_POST_CLIENT_PROFILE_ROUTE)


            .get("/{realm}")
                .id(ApiFortIds.REST_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID)
                .description("Get ApiFort Profile by realm")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.NO_CONTENT).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientProfileDetailsResponse.class).endResponseMessage()
                .outType(ClientProfileDetailsResponse.class)
            .to(GetClientProfileRoute.DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)

            .delete("/{client_profile_uuid}")
                .id(ApiFortIds.REST_DELETE_CLIENT_PROFILE_ROUTE)
                .description("Delete ApiFort Profile")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message("Success").responseModel(GeneralResponse.class).endResponseMessage()
                .outType(GeneralResponse.class)
            .to(DeleteClientProfileRoute.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);


        //CLIENT ENDPOINT REST SERVICE(s)
        rest("/admin-api/{realm}/endpoints")
                .description("ApiFort user define Endpoints")
                .tag("APIFort Profile Endpoints")

            //GET REALM ENDPOINT(s)
            .get()
                .id(ApiFortIds.REST_GET_CLIENT_ENDPOINT_ROUTE_ID)
                .description("ApiFort GET user defined endpoint by using profile uuid")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.NO_CONTENT).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientEndpointResponse.class).endResponseMessage()
                .outType(List.class)
            .to(GetClientEndpointRouter.DIRECT_GET_CLIENT_ENDPOINT_ROUTE)

            //POST CLIENT DEFINE ENDPOINT
            .post()
                .id(ApiFortIds.REST_POST_CLIENT_ENDPOINT_ROUTE_ID)
                .description("ApiFort POST user defined endpoint")
                .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientEndpointResponse.class).endResponseMessage()
                .type(PostEndpointRequest.class)
                .outType(ClientEndpointResponse.class)
            .to(PostClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE)

            //DELETE CLIENT ENDPOINT
            .delete("/{uuid}")
                .id(ApiFortIds.REST_DELETE_CLIENT_ENDPOINT_ROUTER_ID)
                .description("ApiFort DELETE user defined endpoint")
                .consumes(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(GeneralResponse.class).endResponseMessage()
                .outType(GeneralResponse.class)
            .to(DeleteClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);


        //REDIS CACHE SERVICES
        rest("/admin-api/cache/")
                .description("APIFort Cache Endpoints")
                .tag("APIFort Cache")

            .delete("/{cache_key}")
                .id(ApiFortIds.REST_DELETE_ITEM_CACHE_ROUTE_ID)
                .description("Delete cache data by key")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message(ApiFortStatusCode.OK_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .outType(GeneralResponse.class)
            .to(RedisCacheRouter.DIRECT_DELETE_ITEM_CACHE_ROUTE)

            .delete("/{cache_key}/{cache_value}")
                .id(ApiFortIds.REST_DELETE_LIST_CACHE_ROUTE_ID)
                .description("Delete cache data from list using key and value")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message(ApiFortStatusCode.OK_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .outType(GeneralResponse.class)
            .to(RedisCacheRouter.DIRECT_DELETE_LIST_CACHE_ROUTE)

            .post("/{cache_realm}")
                .id(ApiFortIds.REST_SYNC_CACHE_ROUTE_ID)
                .description("Sync Realm data")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message(ApiFortStatusCode.OK_STRING).responseModel(GeneralResponse.class).endResponseMessage()
                .outType(GeneralResponse.class)
            .to(RedisCacheRouter.DIRECT_SYNC_CACHE_ROUTE);

    }
}
