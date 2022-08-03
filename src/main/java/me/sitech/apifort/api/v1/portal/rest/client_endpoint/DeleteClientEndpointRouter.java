package me.sitech.apifort.api.v1.portal.rest.client_endpoint;

import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.portal.dao.ClientEndpointPanacheEntity;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.portal.domain.response.DefaultResponse;
import me.sitech.apifort.security.JwtAuthenticationRoute;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class DeleteClientEndpointRouter extends RouteBuilder {
    public static final String CLIENT_ENDPOINT_UUID = "uuid";

    public static final String DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER = "direct:delete-client-endpoint-route";
    private static final String DELETE_CLIENT_ENDPOINT_ROUTER_ID = "delete-client-endpoint-route-id";

    @Inject
    private ExceptionProcessor processor;

    @Inject
    private RedisClient redisClient;


    @Override
    public void configure() throws Exception {
        onException(Exception.class).handled(true).process(processor).marshal().json();

        from(DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)
                .routeId(DELETE_CLIENT_ENDPOINT_ROUTER_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .log("UUID is ${headers.uuid}")
                .process(exchange -> {
                    String uuid = exchange.getIn().getHeader(CLIENT_ENDPOINT_UUID, String.class);
                    //String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER, String.class);
                    if (uuid == null || uuid.isEmpty()) {
                        throw new APIFortGeneralException("UUID is missing");
                    }
                    ClientEndpointPanacheEntity endpointEntityResult = ClientEndpointPanacheEntity.findByUuid(uuid);
                    if(endpointEntityResult==null)
                        throw new APIFortGeneralException("Failed to delete records");
                    ClientProfilePanacheEntity clientProfileEntityResult = ClientProfilePanacheEntity.findByUuid(endpointEntityResult.getClientProfileFK());

                    ClientEndpointPanacheEntity.terminate(uuid);
                    /*if(ClientEndpointPanacheEntity.findByUuid(uuid)!=null){
                        throw new APIFortGeneralException("Failed to delete endpoint");
                    }*/
                    String cacheKey = String.format("%s-%s",endpointEntityResult.getMethodType().toUpperCase(),clientProfileEntityResult.getApiKey());
                    log.info(">>>>>>>>>>> cacheKey is: {} - Endpoint is: {}",cacheKey,endpointEntityResult.getEndpointPath());
                    redisClient.lrem(cacheKey,"0",endpointEntityResult.getEndpointPath());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.OK);
                    exchange.getIn().setBody(new DefaultResponse(StatusCode.OK, "Client Profile Deleted Successfully"));
                })
                .marshal().json();
    }
}
