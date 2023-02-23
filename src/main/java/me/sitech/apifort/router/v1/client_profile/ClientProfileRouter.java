package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.domain.request.PostCopyEndpointReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.APIFortPathNotFoundException;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_endpoint.processor.CloneEndpointProcessor;
import me.sitech.apifort.router.v1.client_profile.processor.ClientProfileProcessor;
import me.sitech.apifort.router.v1.client_profile.processor.ClientProfileUpdateProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClientProfileRouter extends RouteBuilder {

    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE = "direct:get-client-profile-route";
    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE_ID = "get-client-profile-route-id";

    public static final String DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE = "direct:get-client-profile-by-realm-route";
    public static final String DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID = "et-client-profile-by-realm-route-id";

    public static final String DIRECT_POST_CLIENT_PROFILE_ROUTE = "direct:post-client-profile-route";
    public static final String DIRECT_POST_CLIENT_PROFILE_ROUTE_ID = "post-client-profile-route-id";

    public static final String DIRECT_POST_COPY_CLIENT_ENDPOINT_ROUTE = "direct:post-copy-client-endpoint-route";
    public static final String DIRECT_POST_COPY_CLIENT_ENDPOINT_ROUTE_ID = "post-copy-client-endpoint-route-id";

    public static final String DIRECT_DELETE_CLIENT_PROFILE_ROUTE = "direct:delete-client-profile-route";
    private static final String DIRECT_DELETE_CLIENT_PROFILE_ROUTE_ID = "delete-client-profile-route-id";

    public static final String DIRECT_PUT_CLIENT_PROFILE_ROUTE = "direct:put-client-profile-route";
    private static final String PUT_CLIENT_PROFILE_ROUTE_ID = "put-client-profile-route-id";

    private static final String POST_JSON_VALIDATOR = "json-validator:json/post-profile-validator.json";

    private final CacheClient cacheClient;
    private final ClientProfileProcessor profileProcessor;
    private final CloneEndpointProcessor cloneProcessor;
    private final ClientProfileUpdateProcessor clientProfileUpdateProcessor;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientProfileRouter(CacheClient cacheClient,
                               ClientProfileProcessor profileProcessor,
                               CloneEndpointProcessor cloneProcessor,
                               ClientProfileUpdateProcessor clientProfileUpdateProcessor,
                               ExceptionHandlerProcessor exception){
        this.cacheClient = cacheClient;
        this.profileProcessor = profileProcessor;
        this.cloneProcessor = cloneProcessor;
        this.clientProfileUpdateProcessor =clientProfileUpdateProcessor;
        this.exception = exception;
    }

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
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, entity.isPresent()? ApiFortStatusCode.OK:
                                ApiFortStatusCode.BAD_REQUEST);
                        exchange.getIn().setBody(entity.isPresent()?ClientProfileMapper.mapClientProfileRes(entity.get()):
                                new GeneralRes(ApiFortStatusCode.BAD_REQUEST,"No Data found"));
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
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                        exchange.getIn().setBody(ClientProfileMapper.mapClientProfileRes(entity));
                        return;
                    }
                    throw new APIFortGeneralException("Missing Realm");
                }).marshal().json();


        from(DIRECT_POST_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_POST_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG,"${body}").unmarshal().json(PostClientProfileReq.class)
                .process(profileProcessor).marshal().json();

        from(DIRECT_POST_COPY_CLIENT_ENDPOINT_ROUTE)
                .id(DIRECT_POST_COPY_CLIENT_ENDPOINT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .unmarshal().json(PostCopyEndpointReq.class)
                .process(cloneProcessor).marshal().json();

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
                    cacheClient.removeCacheProfile(clientProfile.get().getApiKey());
                    cacheClient.RemoveCacheByApiKey(clientProfile.get().getApiKey());

                    //Delete Database
                    EndpointPanacheEntity.deleteByClientProfileUuidFK(clientUuidFk);
                    ServicePanacheEntity.deleteByProfileUuid(clientUuidFk);
                    ClientProfilePanacheEntity.deleteByUuid(clientUuidFk);

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "Client Profile Deleted Successfully"));
                }).marshal().json();

        from(DIRECT_PUT_CLIENT_PROFILE_ROUTE)
                .routeId(PUT_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .unmarshal().json(PostClientProfileReq.class)
                .process(clientProfileUpdateProcessor)
                .marshal().json();
    }
}
