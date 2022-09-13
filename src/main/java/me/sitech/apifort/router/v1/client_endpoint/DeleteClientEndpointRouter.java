package me.sitech.apifort.router.v1.client_endpoint;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.response.common.GeneralResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class DeleteClientEndpointRouter extends RouteBuilder {

    public static final String CLIENT_ENDPOINT_UUID = "uuid";
    public static final String DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER = "direct:delete-client-endpoint-route";
    public static final String DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER_ID = "delete-client-endpoint-route-id";

    @Inject
    private ExceptionHandlerProcessor exception;

    @Inject
    private ApiFortCache redisClient;


    @Override
    public void configure() throws Exception {
        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)
                .routeId(DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER_ID)
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
                    EndpointPanacheEntity.terminate(uuid);
                    if(EndpointPanacheEntity.findByUuid(uuid)!=null){
                        throw new APIFortGeneralException("Failed to delete endpoint");
                    }
                    Optional<ClientProfilePanacheEntity> clientProfileEntityResult = ClientProfilePanacheEntity.findByUuid(endpointEntityResult.getClientProfileFK());
                    if(clientProfileEntityResult.isEmpty())
                        return;
                    redisClient.deleteProfileEndpoint(clientProfileEntityResult.get().getApiKey(),
                            endpointEntityResult.getContextPath(),
                            endpointEntityResult.getMethodType(),
                            endpointEntityResult.getEndpointRegex());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralResponse(ApiFortStatusCode.OK, "Client Profile Deleted Successfully"));
                }).marshal().json();
    }
}
