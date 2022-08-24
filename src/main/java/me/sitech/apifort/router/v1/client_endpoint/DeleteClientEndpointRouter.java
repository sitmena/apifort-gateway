package me.sitech.apifort.router.v1.client_endpoint;

import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.response.common.DefaultResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.processor.ExceptionProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;

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

                    if (uuid == null || uuid.isEmpty()) {
                        throw new APIFortGeneralException("UUID is missing");
                    }
                    EndpointPanacheEntity endpointEntityResult = EndpointPanacheEntity.findByUuid(uuid);
                    if(endpointEntityResult==null)
                        throw new APIFortGeneralException("Failed to delete records");
                    ClientProfilePanacheEntity clientProfileEntityResult = ClientProfilePanacheEntity.findByUuid(endpointEntityResult.getClientProfileFK());
                    EndpointPanacheEntity.terminate(uuid);
                    if(EndpointPanacheEntity.findByUuid(uuid)!=null){
                        throw new APIFortGeneralException("Failed to delete endpoint");
                    }
                    String cacheKey = String.format("%s-%s",endpointEntityResult.getMethodType().toUpperCase(),clientProfileEntityResult.getApiKey());
                    String cacheHashKey = String.format("%s%s",endpointEntityResult.getMethodType().toUpperCase(),endpointEntityResult.getEndpointRegex());

                    redisClient.lrem(cacheKey,"0",endpointEntityResult.getEndpointRegex());
                    redisClient.del(Collections.singletonList(new DigestUtils("SHA-1").digestAsHex(cacheHashKey)));

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.OK);
                    exchange.getIn().setBody(new DefaultResponse(StatusCode.OK, "Client Profile Deleted Successfully"));
                })
                .marshal().json();
    }
}
