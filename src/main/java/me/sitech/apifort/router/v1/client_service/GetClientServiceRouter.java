package me.sitech.apifort.router.v1.client_service;

import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.response.service.GetClientServiceRes;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class GetClientServiceRouter extends RouteBuilder {

    public static final String DIRECT_GET_CLIENT_SERVICE_ROUTE = "direct:get-client-service-route";
    public static final String DIRECT_GET_CLIENT_SERVICE_ROUTE_ID = "get-client-service-route-id";

    private final ExceptionHandlerProcessor exception;

    @Inject
    public GetClientServiceRouter(ExceptionHandlerProcessor exception) {
        this.exception = exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_GET_CLIENT_SERVICE_ROUTE)
                .id(DIRECT_GET_CLIENT_SERVICE_ROUTE_ID)
            .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    String realm = exchange.getIn().getHeader("realm",String.class);
                    String profileUuid = ClientProfilePanacheEntity.findByRealm(realm).getUuid();
                    List<GetClientServiceRes> getClientServiceResList = new ArrayList<>();

                    ServicePanacheEntity.findByClientProfileFK(profileUuid).parallelStream().forEach(item->
                            getClientServiceResList.add(new GetClientServiceRes(
                            item.getUuid(),item.getTitle(),item.getDescription(),
                            item.getPath(),item.getContext(),item.getCreatedDate(),item.getUpdatedDate())));
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE,getClientServiceResList.isEmpty() ?
                            ApiFortStatusCode.NO_CONTENT :
                            ApiFortStatusCode.OK);
                    exchange.getIn().setBody(getClientServiceResList);

            }).marshal().json();
    }
}
