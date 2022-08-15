package me.sitech.apifort.processor;

import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.ClientProfileRequest;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ClientProfileUpdateProcessor implements Processor {

    @Inject
    private RedisClient redisClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        ClientProfileRequest request = exchange.getIn().getBody(ClientProfileRequest.class);
        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);
        redisClient.set(Arrays.asList(entity.getApiKey(), Util.unescape(entity.getPublicCertificate())));
    }


    private ClientProfilePanacheEntity clientProfileEntityMapping(ClientProfileRequest request) {
        log.info(">>>>>>>>>> Request is {}",request);
        String generatedUuid = UUID.randomUUID().toString();
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
