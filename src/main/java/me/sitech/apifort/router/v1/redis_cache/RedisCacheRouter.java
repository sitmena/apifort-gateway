package me.sitech.apifort.router.v1.redis_cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.response.cache.CacheEndpointRes;
import me.sitech.apifort.domain.response.cache.CacheRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class RedisCacheRouter extends RouteBuilder {

    public static final String DIRECT_DELETE_ITEM_CACHE_ROUTE = "direct:delete-item-cache-route";
    public static final String DIRECT_DELETE_ITEM_CACHE_ROUTE_ID = "delete-item-cache-route-id";

    public static final String DIRECT_DELETE_LIST_CACHE_ROUTE = "direct:delete-list-cache-route";
    public static final String DIRECT_DELETE_LIST_CACHE_ROUTE_ID = "delete-list-cache-route-id";

    public static final String DIRECT_SYNC_CACHE_ROUTE = "direct:sync-cache-route";
    public static final String DIRECT_SYNC_CACHE_ROUTE_ID = "sync-cache-route-id";

    @Inject
    private ApiFortCache redisClient;

    @Inject
    private ExceptionHandlerProcessor exception;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_DELETE_ITEM_CACHE_ROUTE)
                .id(DIRECT_DELETE_ITEM_CACHE_ROUTE_ID)
            .process(exchange -> {
                String cacheKey = exchange.getIn().getHeader("cache_key",String.class);
                redisClient.del(cacheKey);
            });

        from(DIRECT_DELETE_LIST_CACHE_ROUTE)
            .id(DIRECT_DELETE_LIST_CACHE_ROUTE_ID)
            .process(exchange -> {
                String cacheKey = exchange.getIn().getHeader("cache_key",String.class);
                String cacheValue = exchange.getIn().getHeader("cache_value",String.class);
                redisClient.lDel(cacheKey,cacheValue);
            });

        from(DIRECT_SYNC_CACHE_ROUTE).id(DIRECT_SYNC_CACHE_ROUTE_ID)
            .process(exchange -> {
                //Sync Profile Certificate
                String realm = exchange.getIn().getHeader("cache_realm",String.class);
                ClientProfilePanacheEntity profile = ClientProfilePanacheEntity.findByRealm(realm);

                CacheRes res = new CacheRes();
                res.setApiKey(profile.getApiKey());
                res.setCertificate(profile.getPublicCertificate());

                redisClient.addProfileCertificate(profile.getApiKey(),profile.getPublicCertificate());

                List<EndpointPanacheEntity> endpoints = EndpointPanacheEntity.findByClientProfileFK(profile.getUuid());
                List<ServicePanacheEntity> servicePanacheEntityList = ServicePanacheEntity.findByClientProfileFK(profile.getUuid());

                endpoints.parallelStream().forEach(endpoint->{
                    try {
                        Optional<ServicePanacheEntity> item = servicePanacheEntityList.parallelStream().filter(obj-> endpoint.getServiceUuidFk().equals(obj.getUuid())).findFirst();
                        if (item.isEmpty())
                            throw new APIFortGeneralException("Failed to sync records");
                        CacheEndpointRes cacheEndpointRes = redisClient.addProfileEndpoint(profile.getApiKey(),
                                item.get().getContext(),endpoint.getMethodType(),
                                endpoint.getEndpointRegex(),new ObjectMapper().writeValueAsString(endpoint));
                        res.getCacheEndpoints().add(cacheEndpointRes);
                    } catch (JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                });
                exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
                exchange.getIn().setBody(res);
        }).marshal().json();
    }
}
