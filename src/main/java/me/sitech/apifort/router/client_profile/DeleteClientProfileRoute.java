package me.sitech.apifort.router.client_profile;

import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.response.DefaultResponse;
import me.sitech.apifort.router.security.JwtAuthenticationRoute;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class DeleteClientProfileRoute extends RouteBuilder {
    public static final String DIRECT_DELETE_CLIENT_PROFILE_ROUTE = "direct:delete-client-profile-route";
    private static final String DIRECT_DELETE_CLIENT_PROFILE_ROUTE_ID = "delete-client-profile-route-id";

    @Inject
    private ExceptionProcessor exception;

    @Inject
    private RedisClient redisClient;


    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_DELETE_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_DELETE_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String clientProfileUUID = exchange.getIn().getHeader("client_profile_uuid", String.class);
                    log.info(">>>>>> profile_uuid is {}",clientProfileUUID);
                    ClientProfilePanacheEntity entity = ClientProfilePanacheEntity.findByUuid(clientProfileUUID);
                    ClientProfilePanacheEntity.terminate(clientProfileUUID);
                    redisClient.del(List.of(entity.getApiKey()));
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.OK);
                    exchange.getIn().setBody(new DefaultResponse(StatusCode.OK, "Client Profile Deleted Successfully"));
                })
                .marshal().json();
    }
}
