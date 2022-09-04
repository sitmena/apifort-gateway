package me.sitech.apifort.router.v1;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortIds;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.DefaultResponse;
import me.sitech.apifort.processor.ExceptionProcessor;
import me.sitech.apifort.router.v1.gateway.GatewayRouter;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

@Slf4j
@ApplicationScoped
public class ApplicationRouter extends RouteBuilder {

    @Inject
    private ExceptionProcessor processor;


    @ConfigProperty(name = "apifort.admin.public-context")
    public String publicContextConfigVal;

    @ConfigProperty(name = "apifort.admin.private-context")
    public String privateContextConfigVal;


    @Override
    public void configure() throws Exception {
        
        onException(Exception.class).handled(true).process(processor).marshal().json();

        //APIFORT ROUTER PRIVATE SERVICE(s)
        rest(String.format("/%s/*", privateContextConfigVal))
                .description("APIFort Secure Gateway Entry Points")
                .tag("APIFort Gateway (Private)")

             .get()
                .id(ApiFortIds.REST_GET_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private GET Gateway")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
             .to(GatewayRouter.GET_DIRECT_SECURE_API_GATEWAY_ROUTE)

             .post()
                .id(ApiFortIds.REST_POST_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private POST Gateway")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
             .to(GatewayRouter.POST_DIRECT_SECURE_API_GATEWAY_ROUTE)

             .delete()
                .id(ApiFortIds.REST_DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private Delete Gateway")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
             .to(GatewayRouter.DELETE_DIRECT_SECURE_API_GATEWAY_ROUTE)

             .put()
                .id(ApiFortIds.REST_PUT_DIRECT_SECURE_API_GATEWAY_ROUTE_ID)
                .description("Private PUT Gateway")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
             .to(GatewayRouter.PUT_DIRECT_SECURE_API_GATEWAY_ROUTE);


        //APIFORT ROUTER PUBLIC SERVICES
        rest(String.format("/%s/*",publicContextConfigVal))
                .description("APIFort Public Gateway Entry Points")
                .tag("APIFort Gateway (Public)")

                .get()
                .id(ApiFortIds.REST_GET_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .description("Public GET Gateway")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.GET_DIRECT_GUEST_API_GATEWAY_ROUTE)

                .post()
                .id(ApiFortIds.REST_POST_DIRECT_GUEST_API_GATEWAY_ROUTE_ID)
                .description("Public POST Gateway")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(DefaultResponse.class).endResponseMessage()
                .to(GatewayRouter.POST_DIRECT_GUEST_API_GATEWAY_ROUTE);
    }
}
