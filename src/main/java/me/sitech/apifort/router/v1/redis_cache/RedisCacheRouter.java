package me.sitech.apifort.router.v1.redis_cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.codec.digest.DigestUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class RedisCacheRouter extends RouteBuilder {

    public static final String DIRECT_DELETE_ITEM_CACHE_ROUTE = "direct:delete-item-cache-route";
    public static final String DIRECT_DELETE_ITEM_CACHE_ROUTE_ID = "delete-item-cache-route-id";

    public static final String DIRECT_DELETE_LIST_CACHE_ROUTE = "direct:delete-list-cache-route";
    public static final String DIRECT_DELETE_LIST_CACHE_ROUTE_ID = "delete-list-cache-route-id";

    public static final String DIRECT_SYNC_CACHE_ROUTE = "direct:sync-cache-route";
    public static final String DIRECT_SYNC_CACHE_ROUTE_ID = "sync-cache-route-id";

    @Inject
    private RedisClient redisClient;

    @Override
    public void configure() throws Exception {

        from(DIRECT_DELETE_ITEM_CACHE_ROUTE)
            .id(DIRECT_DELETE_ITEM_CACHE_ROUTE_ID)
            .process(exchange -> {
                String cacheKey = exchange.getIn().getHeader("cache_key",String.class);
                redisClient.del(List.of(cacheKey));
            });

        from(DIRECT_DELETE_LIST_CACHE_ROUTE)
            .id(DIRECT_DELETE_LIST_CACHE_ROUTE_ID)
            .process(exchange -> {
                String cacheKey = exchange.getIn().getHeader("cache_key",String.class);
                String cacheValue = exchange.getIn().getHeader("cache_value",String.class);
                redisClient.hdel(Arrays.asList(cacheKey,cacheValue));
            });

        from(DIRECT_SYNC_CACHE_ROUTE).id(DIRECT_SYNC_CACHE_ROUTE_ID)
                .process(exchange -> {
                    //Sync Profile Certificate
                    String realm = exchange.getIn().getHeader("cache_realm",String.class);
                    ClientProfilePanacheEntity profile = ClientProfilePanacheEntity.findByRealm(realm);
                    if(profile==null)
                        throw new APIFortGeneralException("Invalid Realm");
                    redisClient.set(Arrays.asList(profile.getApiKey(),profile.getPublicCertificate()));

                    //Sync Endpoints group with regex
                    List<EndpointPanacheEntity> endpoints = EndpointPanacheEntity.findByClientProfileFK(profile.getUuid());
                    endpoints.parallelStream().forEach(endpoint->{
                        String groupKey  = Util.redisEndpointGroupCacheId(profile.getApiKey(),endpoint.getContextPath(),endpoint.getMethodType());
                        String endpointUniqueId =Util.regexEndpointUniqueCacheId(profile.getApiKey()
                                ,endpoint.getContextPath(),endpoint.getMethodType(),endpoint.getEndpointRegex());

                        redisClient.lpush(Arrays.asList(groupKey,endpoint.getEndpointRegex()));
                        try {
                            redisClient.set(Arrays.asList(Util.getSHA1(endpointUniqueId),new ObjectMapper().writeValueAsString(endpoint)));
                        }catch (Exception e){
                            log.error(e.getMessage());
                        }
                    });
        });
    }
}
