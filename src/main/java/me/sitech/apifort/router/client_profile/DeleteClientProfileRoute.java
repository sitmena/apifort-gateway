package me.sitech.apifort.router.client_profile;

import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.response.DefaultResponse;
import me.sitech.apifort.domain.response.ErrorResponse;
import me.sitech.apifort.router.security.JwtAuthenticationRoute;
import me.sitech.apifort.constant.ApiFort;
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
    private ExceptionProcessor exceptionProcessor;

    @Inject
    private RedisClient redisClient;


    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();

        from(DIRECT_DELETE_CLIENT_PROFILE_ROUTE)
                .routeId(DIRECT_DELETE_CLIENT_PROFILE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER, String.class);
                    if (apiKey == null || apiKey.isEmpty()) {
                        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.UNAUTHORIZED);
                        exchange.getIn().setBody(new ErrorResponse(StatusCode.UNAUTHORIZED, "Missing api-key"));
                    } else {
                        ClientProfilePanacheEntity.terminate(apiKey);

                        if (null == ClientProfilePanacheEntity.findByApiKey(apiKey)) {
                            redisClient.del(List.of(apiKey));
                            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.OK);
                            exchange.getIn().setBody(new DefaultResponse(StatusCode.OK, "Client Profile Deleted Successfully"));
                        }else{
                            exchange.getIn().setBody(new DefaultResponse(StatusCode.BAD_REQUEST, "Failed to delete client profile"));
                        }
                    }
                })
                .marshal().json();
    }
}
