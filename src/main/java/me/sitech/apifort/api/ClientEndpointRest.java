package me.sitech.apifort.api;

import me.sitech.apifort.constant.ApiFortCamelRestIds;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointDetailsRes;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_endpoint.ClientEndpointRouter;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;


@ApplicationScoped
public class ClientEndpointRest extends RouteBuilder {

    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientEndpointRest(ExceptionHandlerProcessor exception){
        this.exception =exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest("/admin-api/{realm}/endpoints")
                .description("ApiFort user define Endpoints")
                .tag("APIFort Profile Endpoints")

                //GET REALM ENDPOINT(s)
                .get()
                .id(ApiFortCamelRestIds.REST_GET_CLIENT_ENDPOINT_ROUTE_ID)
                .description("ApiFort GET user defined endpoint by using profile uuid")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.NO_CONTENT).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientEndpointRes.class).endResponseMessage()
                .outType(ClientEndpointDetailsRes[].class)
                .to(ClientEndpointRouter.DIRECT_GET_CLIENT_ENDPOINT_ROUTE)

                //POST CLIENT DEFINE ENDPOINT
                .post()
                .id(ApiFortCamelRestIds.REST_POST_CLIENT_ENDPOINT_ROUTE_ID)
                .description("ApiFort POST user defined endpoint")
                .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientEndpointRes.class).endResponseMessage()
                .type(PostEndpointReq.class)
                .outType(ClientEndpointRes.class)
                .to(ClientEndpointRouter.DIRECT_POST_CLIENT_ENDPOINT_ROUTE)

                //DELETE CLIENT ENDPOINT
                .delete("/{uuid}")
                .id(ApiFortCamelRestIds.REST_DELETE_CLIENT_ENDPOINT_ROUTER_ID)
                .description("ApiFort DELETE user defined endpoint")
                .consumes(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(GeneralRes.class).endResponseMessage()
                .outType(GeneralRes.class)
                .to(ClientEndpointRouter.DIRECT_DELETE_CLIENT_ENDPOINT_ROUTER);
    }
}
