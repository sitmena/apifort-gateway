package me.sitech.apifort.router.v1.health_check;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.DefaultResponse;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@ApplicationScoped
public class LiveRoute extends RouteBuilder {

    public static final String DIRECT_GET_HEALTH_ROUTE = "direct:get-health-route";
    public static final String DIRECT_GET_HEALTH_ROUTE_ID = "get-health-route-id";
    private static final String DAY_TIME_FORMAT = "dd-MM-yyyy 'at' HH:mm:ss z";

    @Inject
    private ApiFortCache apiFortCache;
    @Inject
    private ExceptionHandlerProcessor processor;

    @Override
    public void configure() {
        onException(Exception.class).handled(true).process(processor).marshal().json();

        from(DIRECT_GET_HEALTH_ROUTE)
                .routeId(DIRECT_GET_HEALTH_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                    SimpleDateFormat formatter = new SimpleDateFormat(DAY_TIME_FORMAT);
                    Date date = new Date(System.currentTimeMillis());
                    exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                    exchange.getIn().setBody(new DefaultResponse(ApiFortStatusCode.OK, formatter.format(date)));
                }).marshal().json();
    }
}
