package me.sitech.apifort.api;

import me.sitech.apifort.constant.ApiFortCamelRestIds;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.request.PostCopyEndpointReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_profile.ClientProfileRouter;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class ClientGeneral extends RouteBuilder {

    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientGeneral(ExceptionHandlerProcessor exception){
        this.exception=exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest("/admin-api/clone")
                .description("APIFort Clone Endpoints")
                .tag("APIFort Clone")
            .post()
                .id(ApiFortCamelRestIds.REST_POST_COPY_ROUTE_ID)
                .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(GeneralRes.class).endResponseMessage()
                .type(PostCopyEndpointReq.class)
                .outType(GeneralRes.class)
                .to(ClientProfileRouter.DIRECT_POST_COPY_CLIENT_ENDPOINT_ROUTE);
    }
}
