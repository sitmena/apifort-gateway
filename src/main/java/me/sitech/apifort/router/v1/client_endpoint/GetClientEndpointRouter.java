package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointDetailsRes;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetClientEndpointRouter extends RouteBuilder {

    public static final String DIRECT_GET_CLIENT_ENDPOINT_ROUTE = "direct:get-client-endpoint-route";
    public static final String DIRECT_GET_CLIENT_ENDPOINT_ROUTE_ID = "get-client-endpoint-route-id";


    @Inject
    private ExceptionHandlerProcessor exception;

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
                    entityList.stream().parallel().forEach(entityItem -> responseList.add(entityToResponseMapper(entityItem)));
                    exchange.getIn().setBody(responseList);
                }).marshal().json();
    }

    private static ClientEndpointDetailsRes entityToResponseMapper(EndpointPanacheEntity entity) {
        return new ClientEndpointDetailsRes()
                .withUuid(entity.getUuid())
                .withEndpointPath(entity.getEndpointPath())
                .withMethodType(entity.getMethodType())
                .withAuthClaimValue(entity.getAuthClaimValue())
                .withEndpointRegex(entity.getEndpointRegex())
                .withVersionNumber(entity.getVersionNumber())
                .withOfflineAuthentication(entity.isOfflineAuthentication())
                .withCreatedDate(entity.getCreatedDate())
                .withUpdatedDate(entity.getUpdatedDate());
    }
}
