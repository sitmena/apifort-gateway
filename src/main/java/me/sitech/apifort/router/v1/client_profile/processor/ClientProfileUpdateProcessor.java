package me.sitech.apifort.router.v1.client_profile.processor;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.domain.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.router.v1.client_profile.ClientProfileMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@Slf4j
@ApplicationScoped
public class ClientProfileUpdateProcessor implements Processor {

    private final CacheClient redisClient;

    @Inject
    public ClientProfileUpdateProcessor(CacheClient redisClient){
        this.redisClient =redisClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        PostClientProfileReq request = exchange.getIn().getBody(PostClientProfileReq.class);
        ClientProfilePanacheEntity entity = ClientProfileMapper.mapClientProfileEntity(request);
        redisClient.cachePublicCertificate(entity.getApiKey(),entity.getPublicCertificate(),entity.getRealm());
    }

}
