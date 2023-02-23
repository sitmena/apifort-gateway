package me.sitech.apifort.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortCamelRestIds;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.health.LiveRoute;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class HealthRest extends RouteBuilder {

    private final ExceptionHandlerProcessor exception;

    @Inject
    public HealthRest(ExceptionHandlerProcessor exception){
        this.exception=exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest("/live")
                .tag("APIFort Health")
                .description("APIFort Health Endpoint")
             .get()
                .id(ApiFortCamelRestIds.REST_GET_HEALTH_ROUTE_ID)
                .description("Health Check REST service")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(GeneralRes.class).endResponseMessage()
                .outType(GeneralRes.class)
            .to(LiveRoute.DIRECT_GET_HEALTH_ROUTE);
    }
}
