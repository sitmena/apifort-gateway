package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointDetailsRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_endpoint.processor.CreateEndpointProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ClientEndpointRouter extends RouteBuilder {

    public static final String CLIENT_ENDPOINT_UUID = "uuid";

    public static final String DIRECT_GET_CLIENT_ENDPOINT_ROUTE = "direct:get-client-endpoint-route";
    public static final String DIRECT_GET_CLIENT_ENDPOINT_ROUTE_ID = "get-client-endpoint-route-id";

    public static final String DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER = "direct:delete-client-endpoint-route";
    public static final String DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER_ID = "delete-client-endpoint-route-id";

    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE = "direct:post-client-endpoint-route";
    public static final String DIRECT_POST_CLIENT_ENDPOINT_ROUTE_ID = "post-client-endpoint-route-id";

    private static final String POST_JSON_VALIDATOR = "json-validator:json/post-endpoint-validator.json";

    private final CacheClient redisClient;
    private final ExceptionHandlerProcessor exception;
    private final CreateEndpointProcessor processor;
    @Inject
    public ClientEndpointRouter(CacheClient redisClient,
                                CreateEndpointProcessor processor,
                                ExceptionHandlerProcessor exception){
        this.redisClient = redisClient;
        this.processor =processor;
        this.exception = exception;
    }
    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_GET_CLIENT_ENDPOINT_ROUTE)
                .routeId(DIRECT_GET_CLIENT_ENDPOINT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String realm = exchange.getIn().getHeader("realm", String.class);
                    ClientProfilePanacheEntity clientProfileEntity = ClientProfilePanacheEntity.findByRealm(realm);

                    List<EndpointPanacheEntity> entityList = EndpointPanacheEntity.findByClientProfileFK(clientProfileEntity.getUuid());
                    List<ClientEndpointDetailsRes> responseList = new ArrayList<>();
                    entityList.stream().parallel().forEach(entityItem -> responseList.add(
                            ClientEndpointMapper.entityToResponseMapper(entityItem)));
                    exchange.getIn().setBody(responseList);
                }).marshal().json();


        from(DIRECT_POST_CLIENT_ENDPOINT_ROUTE)
                .routeId(DIRECT_POST_CLIENT_ENDPOINT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_JSON_VALIDATOR)
                .unmarshal().json(PostEndpointReq.class)
                .log(LoggingLevel.DEBUG,"${body}")
                .process(processor)
                .marshal().json();


        from(DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)
                .routeId(DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log(LoggingLevel.DEBUG,"UUID is ${headers.uuid}")
                .process(exchange -> {
                    String uuid = exchange.getIn().getHeader(CLIENT_ENDPOINT_UUID, String.class);
                    if (uuid == null || uuid.isEmpty()) {
                        throw new APIFortGeneralException("UUID is missing");
                    }
                    EndpointPanacheEntity endpointEntityResult = EndpointPanacheEntity.findByUuid(uuid);
                    ServicePanacheEntity servicePanacheEntity = ServicePanacheEntity.findByUuid(endpointEntityResult.getServiceUuidFk());
                    EndpointPanacheEntity.delete(uuid);
                    Optional<ClientProfilePanacheEntity> clientProfileEntityResult = ClientProfilePanacheEntity.findByUuid(endpointEntityResult.getClientUuidFk());
                    if(clientProfileEntityResult.isEmpty())
                        return;
                    redisClient.removeCacheEndpoint(clientProfileEntityResult.get().getApiKey(),
                            servicePanacheEntity.getContext(),
                            endpointEntityResult.getMethodType(),
                            endpointEntityResult.getEndpointRegex());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "Client Profile Deleted Successfully"));
                }).marshal().json();
    }
}
