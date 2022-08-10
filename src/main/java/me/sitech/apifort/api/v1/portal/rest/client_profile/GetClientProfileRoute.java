package me.sitech.apifort.api.v1.portal.rest.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.portal.domain.response.ClientProfileResponse;
import me.sitech.apifort.security.JwtAuthenticationRoute;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class GetClientProfileRoute extends RouteBuilder {
    public static final String DIRECT_GET_CLIENT_PROFILE_ROUTE = "direct:get-client-profile-route";
    private static final String DIRECT_GET_CLIENT_PROFILE_ROUTE_ID = "get-client-profile-route-id";

    @Inject
    private ExceptionProcessor exceptionProcessor;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();

        from(DIRECT_GET_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_GET_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER, String.class);
                    log.info("Request API key is {}", apiKey);
                    if (apiKey == null || apiKey.isEmpty()) {
                        throw new APIFortGeneralException("Missing api-key");
                    } else {
                        ClientProfilePanacheEntity entity = ClientProfilePanacheEntity.findByApiKey(apiKey);
                        ClientProfileResponse response = new ClientProfileResponse();
                        if (entity != null) {
                            response.setClientExternalId(entity.getApiKey());
                            response.setRealm(entity.getApiKey());
                            response.setAuthClaimKey(entity.getAuthClaimKey());
                            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.OK);
                            exchange.getIn().setBody(response);
                        } else {
                            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.NO_CONTENT);
                        }
                    }
                }).marshal().json();
    }
}
