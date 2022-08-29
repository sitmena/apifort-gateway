package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointDetailsResponse;
import org.apache.camel.builder.RouteBuilder;

import java.util.ArrayList;
import java.util.List;

public class GetClientEndpointRouter extends RouteBuilder {

    public static final String DIRECT_GET_CLIENT_ENDPOINT_ROUTE = "direct:get-client-endpoint-route";
    public static final String DIRECT_GET_CLIENT_ENDPOINT_ROUTE_ID  = "get-client-endpoint-route-id";

    @Override
    public void configure() throws Exception {
            from(DIRECT_GET_CLIENT_ENDPOINT_ROUTE)
                    .routeId(DIRECT_GET_CLIENT_ENDPOINT_ROUTE_ID)
                    .process(exchange -> {
                        String clientProfileUuid = exchange.getIn().getHeader("client_profile_uuid",String.class);
                        List<EndpointPanacheEntity> entityList = EndpointPanacheEntity.findByClientProfileFK(clientProfileUuid);
                        List<ClientEndpointDetailsResponse> responseList = new ArrayList<>();
                        entityList.stream().parallel().forEach(entityItem->{
                            responseList.add(mapper(entityItem));
                        });
                        exchange.getIn().setBody(responseList);
                    });
    }

    private static ClientEndpointDetailsResponse mapper(EndpointPanacheEntity entity){
        return new ClientEndpointDetailsResponse()
                .withUuid(entity.getUuid())
                .withClientProfileFK(entity.getClientProfileFK())
                .withServiceName(entity.getServiceName())
                .withEndpointPath(entity.getEndpointPath())
                .withMethodType(entity.getMethodType())
                .withAuthClaimValue(entity.getAuthClaimValue())
                .withOfflineAuthentication(entity.isOfflineAuthentication())
                .withVersionNumber(entity.getVersionNumber())
                .withEndpointRegex(entity.getEndpointRegex());


    }
}
