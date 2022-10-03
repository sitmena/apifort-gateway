package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.exceptions.APIFortPathNotFoundException;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class DeleteClientProfileRoute extends RouteBuilder {
    public static final String DIRECT_DELETE_CLIENT_PROFILE_ROUTE = "direct:delete-client-profile-route";
    private static final String DIRECT_DELETE_CLIENT_PROFILE_ROUTE_ID = "delete-client-profile-route-id";

    private final ApiFortCache redisClient;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public DeleteClientProfileRoute(ApiFortCache redisClient,ExceptionHandlerProcessor exception){
        this.redisClient = redisClient;
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_DELETE_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_DELETE_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String clientUuidFk = exchange.getIn().getHeader(ApiFort.CLIENT_PROFILE_UUID, String.class);

                    log.debug(">>>>>> profile_uuid is {}",clientUuidFk);
                    Optional<ClientProfilePanacheEntity> clientProfile = ClientProfilePanacheEntity.findByUuid(clientUuidFk);
                    if(clientProfile.isEmpty()){
                       throw new APIFortPathNotFoundException("Profile not exist");
                    }
                    //Clear Cash
                    redisClient.deleteProfile(clientProfile.get().getApiKey());
                    redisClient.deleteByApiKey(clientProfile.get().getApiKey());

                    //Delete Database
                    EndpointPanacheEntity.deleteByClientProfileUuidFK(clientUuidFk);
                    ServicePanacheEntity.deleteByProfileUuid(clientUuidFk);
                    ClientProfilePanacheEntity.deleteByUuid(clientUuidFk);

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "Client Profile Deleted Successfully"));
                }).marshal().json();
    }
}
