package me.sitech.apifort.api;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortCamelRestIds;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.response.common.GeneralRes;
import me.sitech.apifort.exceptions.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.router.v1.cache.RedisCacheRouter;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class CacheRest extends RouteBuilder {

    private final ExceptionHandlerProcessor exception;

    @Inject
    public CacheRest(ExceptionHandlerProcessor exception){
        this.exception=exception;
    }

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        rest("/admin-api/cache/")
                .description("APIFort Cache Endpoints")
                .tag("APIFort Cache")

            .delete("/{cache_key}")
                .id(ApiFortCamelRestIds.REST_DELETE_ITEM_CACHE_ROUTE_ID)
                .description("Delete cache data by key")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message(ApiFortStatusCode.OK_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .outType(GeneralRes.class)
                .to(RedisCacheRouter.DIRECT_DELETE_ITEM_CACHE_ROUTE)

            .delete("/{cache_key}/{cache_value}")
                .id(ApiFortCamelRestIds.REST_DELETE_LIST_CACHE_ROUTE_ID)
                .description("Delete cache data from list using key and value")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message(ApiFortStatusCode.OK_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .outType(GeneralRes.class)
                .to(RedisCacheRouter.DIRECT_DELETE_LIST_CACHE_ROUTE)

            .post("/{cache_realm}")
                .id(ApiFortCamelRestIds.REST_SYNC_CACHE_ROUTE_ID)
                .description("Sync Realm data")
                .responseMessage().code(ApiFortStatusCode.UNAUTHORIZED).message(ApiFortStatusCode.UNAUTHORIZED_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .responseMessage().code(ApiFortStatusCode.OK).message(ApiFortStatusCode.OK_STRING).responseModel(GeneralRes.class).endResponseMessage()
                .outType(GeneralRes.class)
                .to(RedisCacheRouter.DIRECT_SYNC_CACHE_ROUTE);

    }
}
