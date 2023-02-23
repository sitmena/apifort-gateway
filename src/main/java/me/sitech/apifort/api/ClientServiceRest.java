package me.sitech.apifort.api;

import me.sitech.apifort.constant.ApiFortIds;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.domain.request.PostClientServiceReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.domain.response.service.GetClientServiceRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_service.ClientServiceRouter;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClientServiceRest extends RouteBuilder {

    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientServiceRest(ExceptionHandlerProcessor exception){
        this.exception =exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();
        rest("/admin-api/{realm}/service")
                .description("ApiFort Profile services")
                .tag("APIFort Profile Services")

                .post()
                .id(ApiFortIds.REST_POST_SERVICE_ROUTE_ID)
                .description("Create service by realm")
                .consumes(ApiFortMediaType.APPLICATION_JSON)
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .outType(GeneralRes.class)
                .type(PostClientServiceReq.class)
                .to(ClientServiceRouter.DIRECT_POST_CLIENT_SERVICE_ROUTE)

                .put()
                .id(ApiFortIds.REST_PUT_SERVICE_ROUTE_ID)
                .description("Update Service details")
                .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                .type(PostClientServiceReq.class)
                .outType(PostClientServiceReq.class)
                .to(ClientServiceRouter.DIRECT_PUT_CLIENT_SERVICE_ROUTE)
                .get()
                .id(ApiFortIds.REST_GET_SERVICE_ROUTE_ID)
                .description("Get all defined services by realm")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .outType(GetClientServiceRes[].class)
                .to(ClientServiceRouter.DIRECT_GET_CLIENT_SERVICE_ROUTE)

                .delete("/{service_context}")
                .id(ApiFortIds.REST_DELETE_SERVICE_ROUTE_ID)
                .description("Delete service by realm and context")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .outType(GeneralRes.class)
                .to(ClientServiceRouter.DIRECT_DELETE_CLIENT_CLIENT_ROUTE);
    }
}
