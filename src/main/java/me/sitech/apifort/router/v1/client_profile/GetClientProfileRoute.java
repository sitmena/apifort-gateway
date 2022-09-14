package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.response.common.GeneralResponse;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.APIFortNoDataFound;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class GetClientProfileRoute extends RouteBuilder {
    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE = "direct:get-client-profile-route";
    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE_ID = "get-client-profile-route-id";

    public static final String DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE = "direct:get-client-profile-by-realm-route";
    public static final String DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID = "et-client-profile-by-realm-route-id";


    @Inject
    private ExceptionHandlerProcessor exception;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();
        from(DIRECT_GET_CLIENT_PROFILE_ROUTE)
            .routeId(DIRECT_GET_CLIENT_PROFILE_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .process(exchange -> {
                String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER, String.class);
                log.info("Request API key is {}", apiKey);
                if(Util.isNotEmpty(apiKey)){
                   Optional<ClientProfilePanacheEntity> entity = ClientProfilePanacheEntity.findByApiKey(apiKey);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, entity.isPresent()?ApiFortStatusCode.OK:ApiFortStatusCode.BAD_REQUEST);
                    exchange.getIn().setBody(entity.isPresent()?GetClientProfileRoute.mapper(entity.get()):new GeneralResponse(ApiFortStatusCode.BAD_REQUEST,"No Data found"));
                    return;
                }
                throw new APIFortGeneralException("Missing api-key");
            }).marshal().json();


        from(DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)
            .routeId(DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .process(exchange -> {
                String realm = exchange.getIn().getHeader("realm", String.class);
                if(Util.isNotEmpty(realm)){
                    ClientProfilePanacheEntity entity = ClientProfilePanacheEntity.findByRealm(realm);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, entity!=null?ApiFortStatusCode.OK:ApiFortStatusCode.BAD_REQUEST);
                    exchange.getIn().setBody(entity!=null?GetClientProfileRoute.mapper(entity):new GeneralResponse(ApiFortStatusCode.BAD_REQUEST,"Realm not exist"));
                    return;
                }
                throw new APIFortGeneralException("Missing Realm");
            }).marshal().json();
    }

    private static ClientProfileDetailsResponse mapper(ClientProfilePanacheEntity entity){
        ClientProfileDetailsResponse response = new ClientProfileDetailsResponse();
        response.setClientProfileUuid(entity.getUuid());
        response.setRealm(entity.getRealm());
        response.setAuthClaimKey(entity.getAuthClaimKey());
        response.setApiKey(entity.getApiKey());
        response.setPublicCertificate(entity.getPublicCertificate());
        return response;
    }
}
