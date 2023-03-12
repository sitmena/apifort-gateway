package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.entity.ClientProfileEntity;
import me.sitech.apifort.domain.entity.EndpointPanacheEntity;
import me.sitech.apifort.domain.entity.ServicePanacheEntity;
import me.sitech.apifort.domain.module.ProfileCounts;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.domain.request.PostCopyEndpointReq;
import me.sitech.apifort.domain.request.PutClientProfileReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.domain.response.common.Pages;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.APIFortPathNotFoundException;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_endpoint.processor.CloneEndpointProcessor;
import me.sitech.apifort.router.v1.client_profile.processor.ClientProfileProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClientProfileRouter extends RouteBuilder {

    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE = "direct:get-client-profile-route";
    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE_ID = "get-client-profile-route-id";

    public static final String DIRECT_GET_CLIENT_PROFILES_ROUTE = "direct:get-client-profiles-route";
    public static final String DIRECT_GET_CLIENT_PROFILES_ROUTE_ID = "et-client-profiles-route-id";

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
    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientProfileRouter(CacheClient cacheClient,
                               ClientProfileProcessor profileProcessor,
                               CloneEndpointProcessor cloneProcessor,
                               ExceptionHandlerProcessor exception){
        this.cacheClient = cacheClient;
        this.profileProcessor = profileProcessor;
        this.cloneProcessor = cloneProcessor;
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
                        Optional<ClientProfileEntity> entity = ClientProfileEntity.findByApiKey(apiKey);
                        if(entity.isPresent()){
                            ProfileCounts counts  = ClientProfileEntity.getCounts(entity.get().getUuid());
                            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                            exchange.getIn().setBody(ClientProfileMapper.mapClientProfileRes(entity.get(),counts.getServiceCount(),
                                    counts.getEndpointCount()));
                            return;
                        }
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE,ApiFortStatusCode.BAD_REQUEST);
                        exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.BAD_REQUEST,"No Data found"));
                        return;
                    }
                    throw new APIFortGeneralException("Missing api-key");
                }).marshal().json();

        from(DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)
                .routeId(DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String realm = exchange.getIn().getHeader(ApiFort.API_REALM, String.class);
                    if(Util.isNotEmpty(realm)){
                        ClientProfileEntity entity = ClientProfileEntity.findByRealm(realm);
                        ProfileCounts counts =  ClientProfileEntity.getCounts(entity.getUuid());
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                        exchange.getIn().setBody(ClientProfileMapper.mapClientProfileRes(entity,counts.getServiceCount(),counts.getEndpointCount()));
                        return;
                    }
                    throw new APIFortGeneralException("Missing Realm");
                }).marshal().json();

        from(DIRECT_GET_CLIENT_PROFILES_ROUTE)
            .id(DIRECT_GET_CLIENT_PROFILES_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    Integer pageIndex = exchange.getIn().getHeader("page", Integer.class);
                    Integer pageSize = exchange.getIn().getHeader("size", Integer.class);
                    String q = exchange.getIn().getHeader("q", String.class);
                    Pages<ClientProfileDetailsRes> pages = new Pages<>();

                    int defaultPageSize = (pageSize==null||pageSize<=0||pageSize>50)?10:pageSize;
                    int defaultPageIndex = (pageIndex==null||pageIndex<0)?0:pageIndex;

                    List<ClientProfileEntity> entityList = ClientProfileEntity.findAll(q,defaultPageIndex,defaultPageSize);
                    List<ProfileCounts> counts  =ClientProfileEntity.getCounts();
                    pages.setT(ClientProfileMapper.mapClientProfileRes(entityList,counts));
                    pages.setPageIndex(defaultPageIndex);
                    pages.setPageSize(defaultPageSize);
                    pages.setTotal(ClientProfileEntity.totalProfiles());
                    exchange.getIn().setBody(pages);
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
                    Optional<ClientProfileEntity> clientProfile = ClientProfileEntity.findByUuid(clientUuidFk);
                    if(clientProfile.isEmpty()){
                        throw new APIFortPathNotFoundException("Profile not exist");
                    }
                    //Clear Cash
                    cacheClient.removeCacheProfile(clientProfile.get().getApiKey());
                    cacheClient.RemoveCacheByApiKey(clientProfile.get().getApiKey());

                    //Delete Database
                    EndpointPanacheEntity.deleteByClientProfileUuidFK(clientUuidFk);
                    ServicePanacheEntity.deleteByProfileUuid(clientUuidFk);
                    ClientProfileEntity.deleteByUuid(clientUuidFk);

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "Client Profile Deleted Successfully"));
                }).marshal().json();

        from(DIRECT_PUT_CLIENT_PROFILE_ROUTE)
                .routeId(PUT_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .unmarshal().json(PutClientProfileReq.class)
                .process(exchange->{
                    String realm = exchange.getIn().getHeader(ApiFort.API_REALM,String.class);
                    PutClientProfileReq req = exchange.getIn().getBody(PutClientProfileReq.class);
                    ClientProfileEntity entity = ClientProfileEntity.findByRealm(realm);
                    if(!req.getClientProfileUuid().equals(entity.getUuid()))
                        throw new APIFortGeneralException("Invalid Profile details");
                    ClientProfileEntity.updateProfile(req.getTitle(),req.getDescription(),
                            req.getAuthClaimKey(), req.getClientProfileUuid(), realm);

                    cacheClient.cachePublicCertificate(entity.getApiKey(),entity.getPublicCertificate(),realm);
                    ProfileCounts counts  = ClientProfileEntity.getCounts(req.getClientProfileUuid());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(ClientProfileMapper.mapClientProfileRes(entity,counts.getServiceCount(),counts.getEndpointCount()));
                })
                .marshal().json();

    }
}
