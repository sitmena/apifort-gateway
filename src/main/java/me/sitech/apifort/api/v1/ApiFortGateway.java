package me.sitech.apifort.api.v1;

import me.sitech.apifort.api.v1.gateway.GatewayRouter;
import me.sitech.apifort.api.v1.portal.domain.response.ErrorResponse;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;

public class ApiFortGateway extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        rest("/api/*")
                .description("API Fort Gateway accept and rout request endpoint").consumes("application/json").produces("application/json")
            .get()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(200).message("The return response from downstream services").endResponseMessage()
                .id("getDownstreamServices")
                .to(GatewayRouter.GET_DIRECT_API_GATEWAY_ROUTE)
            .post()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(200).message("The return response from downstream services").endResponseMessage()
                .id("postDownstreamServices")
                .to(GatewayRouter.POST_DIRECT_API_GATEWAY_ROUTE)
            .put()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(200).message("The return response from downstream services").endResponseMessage()
                .id("putDownstreamServices")
                .to(GatewayRouter.PUT_DIRECT_API_GATEWAY_ROUTE)
            .delete()
                .responseMessage().code(401).responseModel(ErrorResponse.class).endResponseMessage()
                .responseMessage().code(200).message("The return response from downstream services").endResponseMessage()
                .id("deleteDownstreamServices")
                .to(GatewayRouter.DELETE_DIRECT_API_GATEWAY_ROUTE);
    }
}
