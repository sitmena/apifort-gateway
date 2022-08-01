package me.sitech.apifort.api.v1.portal.rest.client_endpoint;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.StatusCode;
import me.sitech.apifort.api.v1.portal.domain.response.DefaultResponse;
import me.sitech.apifort.api.v1.portal.domain.response.ErrorResponse;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class DeleteClientEndpointRouter extends RouteBuilder {
    public static final String CLIENT_ENDPOINT_UUID = "client-endpoint-uuid";

    public static final String DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER = "direct:delete-client-endpoint-route";
    private static final String DELETE_CLIENT_ENDPOINT_ROUTER_ID = "delete-client-endpoint-route-id";

    @Inject
    private ExceptionProcessor processor;

    @Override
    public void configure() throws Exception {
        onException(Exception.class).handled(true).process(processor).marshal().json();

        from(DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER)
            .routeId(DELETE_CLIENT_ENDPOINT_ROUTER_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
            .process(exchange -> {
                String uuid = exchange.getIn().getHeader(CLIENT_ENDPOINT_UUID, String.class);
                if (uuid == null || uuid.isEmpty()) {
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.BAD_REQUEST);
                    exchange.getIn().setBody(new ErrorResponse(StatusCode.BAD_REQUEST, "Missing endpoint uuid"));
                } else {
                    ClientProfilePanacheEntity.terminate(uuid);
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, StatusCode.OK);
                    exchange.getIn().setBody(new DefaultResponse(StatusCode.OK, "Client Profile Deleted Successfully"));
                }
            })
            .marshal().json();
    }
}
