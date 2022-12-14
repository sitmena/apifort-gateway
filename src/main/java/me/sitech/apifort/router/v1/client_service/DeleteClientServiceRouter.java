package me.sitech.apifort.router.v1.client_service;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class DeleteClientServiceRouter extends RouteBuilder {

    public static final String DIRECT_DELETE_CLIENT_CLIENT_ROUTE = "direct:delete-client-service-route";
    private static final String DIRECT_DELETE_CLIENT_ROUTE_ID = "delete-client-service-route-id";

    private final ApiFortCache redisClient;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public DeleteClientServiceRouter(ApiFortCache redisClient,ExceptionHandlerProcessor exception){
        this.redisClient =redisClient;
        this.exception=exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_DELETE_CLIENT_CLIENT_ROUTE)
                .id(DIRECT_DELETE_CLIENT_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String realm = exchange.getIn().getHeader(ApiFort.API_REALM,String.class);
                    String context = exchange.getIn().getHeader(ApiFort.API_CONTEXT,String.class);
                    ClientProfilePanacheEntity clientProfile = ClientProfilePanacheEntity.findByRealm(realm);
                    //Delete Cache Part

                    redisClient.deleteByApiKeyAndContext(clientProfile.getApiKey(),context );
                    //Delete Database Part
                    ServicePanacheEntity.deleteByProfileUuidAndContext(clientProfile.getUuid(),context);

                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "Client Service Deleted Successfully"));

                }).marshal().json();
    }
}
