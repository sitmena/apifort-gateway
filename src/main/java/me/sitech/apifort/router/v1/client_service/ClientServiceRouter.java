package me.sitech.apifort.router.v1.client_service;

import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.entity.ClientProfileEntity;
import me.sitech.apifort.domain.entity.EndpointPanacheEntity;
import me.sitech.apifort.domain.entity.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostClientServiceReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.domain.response.endpoints.GetEndpointRes;
import me.sitech.apifort.domain.response.service.GetClientServiceRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_endpoint.ClientEndpointMapper;
import me.sitech.apifort.router.v1.client_endpoint.ClientEndpointRouter;
import me.sitech.apifort.router.v1.client_service.processor.PostServiceProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ClientServiceRouter extends RouteBuilder {
    public static final String DIRECT_GET_CLIENT_SERVICE_ROUTE = "direct:get-client-service-route";
    public static final String DIRECT_GET_CLIENT_SERVICE_ROUTE_ID = "get-client-service-route-id";


    public static final String DIRECT_POST_CLIENT_SERVICE_ROUTE = "direct:post-client-service-route";
    public static final String DIRECT_POST_CLIENT_SERVICE_ROUTE_ID = "post-client-service-route-id";

    public static final String DIRECT_PUT_CLIENT_SERVICE_ROUTE = "direct:put-client-service-route";
    public static final String DIRECT_PUT_CLIENT_SERVICE_ROUTE_ID = "put-client-service-route-id";

    private static final String POST_JSON_VALIDATOR = "json-validator:json/post-service-validator.json";
    private static final String PUT_JSON_VALIDATOR = "json-validator:json/put-service-validator.json";

    public static final String DIRECT_DELETE_CLIENT_CLIENT_ROUTE = "direct:delete-client-service-route";
    private static final String DIRECT_DELETE_CLIENT_ROUTE_ID = "delete-client-service-route-id";


    private final ProducerTemplate producerTemplate;
    private final CacheClient redisClient;
    private final PostServiceProcessor postServiceProcessor;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientServiceRouter(PostServiceProcessor postServiceProcessor,
                               ProducerTemplate producerTemplate,
                               CacheClient redisClient,
                               ExceptionHandlerProcessor exception) {
        this.postServiceProcessor = postServiceProcessor;
        this.producerTemplate = producerTemplate;
        this.redisClient = redisClient;
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {
        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_GET_CLIENT_SERVICE_ROUTE)
                .id(DIRECT_GET_CLIENT_SERVICE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String realm = exchange.getIn().getHeader(ApiFort.API_REALM, String.class);
                    String profileUuid = ClientProfileEntity.findByRealm(realm).getUuid();
                    List<GetClientServiceRes> getClientServiceResList = new ArrayList<>();

                    List<GetEndpointRes> endpoints =ClientEndpointMapper.mapGetEndpointRes(EndpointPanacheEntity
                            .findByClientProfileFK(profileUuid));

                    ServicePanacheEntity.findByClientProfileFK(profileUuid).parallelStream().forEach(item -> {
                                List<GetEndpointRes> endpointList = null;
                                if (endpoints != null)
                                    endpointList = endpoints.stream()
                                            .filter(index -> item.getUuid().equals(index.getServiceUuid())).collect(Collectors.toList());

                                getClientServiceResList.add(new GetClientServiceRes(
                                        item.getUuid(), item.getTitle(), item.getDescription(),
                                        item.getPath(), item.getContext(), item.getCreatedDate(),
                                        item.getUpdatedDate(), endpointList));
                            }
                    );

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, getClientServiceResList.isEmpty() ?
                            ApiFortStatusCode.NO_CONTENT : ApiFortStatusCode.OK);
                    exchange.getIn().setBody(getClientServiceResList);

                }).marshal().json();

        from(DIRECT_POST_CLIENT_SERVICE_ROUTE)
                .id(DIRECT_POST_CLIENT_SERVICE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(POST_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG, "${body}").unmarshal().json(PostClientServiceReq.class)
                .setHeader(ApiFort.API_FORT_ROUTER_ACTION, constant(ApiFort.API_FORT_CREATE_ACTION))
                .process(postServiceProcessor)
                .marshal().json();


        from(DIRECT_PUT_CLIENT_SERVICE_ROUTE)
                .id(DIRECT_PUT_CLIENT_SERVICE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .to(PUT_JSON_VALIDATOR)
                .log(LoggingLevel.DEBUG, "${body}").unmarshal().json(PostClientServiceReq.class)
                .setHeader(ApiFort.API_FORT_ROUTER_ACTION, constant(ApiFort.API_FORT_UPDATE_ACTION))
                .process(postServiceProcessor)
                .marshal().json();

        from(DIRECT_DELETE_CLIENT_CLIENT_ROUTE)
                .id(DIRECT_DELETE_CLIENT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String realm = exchange.getIn().getHeader(ApiFort.API_REALM, String.class);
                    String context = exchange.getIn().getHeader(ApiFort.API_CONTEXT, String.class);
                    ClientProfileEntity clientProfile = ClientProfileEntity.findByRealm(realm);
                    //Delete Cache Part
                    redisClient.removeCache(clientProfile.getApiKey(), context);
                    //Delete Database Part
                    ServicePanacheEntity.deleteByProfileUuidAndContext(clientProfile.getUuid(), context);

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "Client Service Deleted Successfully"));

                }).marshal().json();
    }
}
