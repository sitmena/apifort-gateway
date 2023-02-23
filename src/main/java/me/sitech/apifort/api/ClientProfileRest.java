package me.sitech.apifort.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortIds;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsRes;
import me.sitech.apifort.domain.response.profile.PostClientProfileRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.client_profile.ClientProfileRouter;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ClientProfileRest extends RouteBuilder {

    private final ExceptionHandlerProcessor exception;

    @Inject
    public ClientProfileRest(ExceptionHandlerProcessor exception){
        this.exception =exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest("/admin-api/profile")
                .description("APIFort Profile Endpoint(s)")
                .tag("APIFort Profiles")
                .post()
                .id(ApiFortIds.REST_POST_CLIENT_PROFILE_ROUTE_ID)
                .description("Post ApiFort Profile")
                .consumes(ApiFortMediaType.APPLICATION_JSON).produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(PostClientProfileRes.class).endResponseMessage()
                .type(PostClientProfileReq.class)
                .outType(PostClientProfileRes.class)
                .to(ClientProfileRouter.DIRECT_POST_CLIENT_PROFILE_ROUTE)


                .get("/{realm}")
                .id(ApiFortIds.REST_GET_CLIENT_PROFILE_BY_REALM_ROUTE_ID)
                .description("Get ApiFort Profile by realm")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.NO_CONTENT).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).responseModel(ClientProfileDetailsRes.class).endResponseMessage()
                .outType(ClientProfileDetailsRes.class)
                .to(ClientProfileRouter.DIRECT_GET_CLIENT_PROFILE_BY_REALM_ROUTE)

                .delete("/{client_profile_uuid}")
                .id(ApiFortIds.REST_DELETE_CLIENT_PROFILE_ROUTE)
                .description("Delete ApiFort Profile")
                .produces(ApiFortMediaType.APPLICATION_JSON)
                .responseMessage().code(ApiFortStatusCode.BAD_REQUEST).message(ApiFortStatusCode.BAD_REQUEST_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message("Success").responseModel(GeneralRes.class).endResponseMessage()
                .outType(GeneralRes.class)
                .to(ClientProfileRouter.DIRECT_DELETE_CLIENT_PROFILE_ROUTE);

    }
}
