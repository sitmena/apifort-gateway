package me.sitech.apifort.processor;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.PostClientProfileRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ClientProfileUpdateProcessor implements Processor {

    @Inject
    private ApiFortCache redisClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        PostClientProfileRequest request = exchange.getIn().getBody(PostClientProfileRequest.class);
        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);

        redisClient.addProfileCertificate(entity.getApiKey(),entity.getPublicCertificate());
    }


    private ClientProfilePanacheEntity clientProfileEntityMapping(PostClientProfileRequest request) {
        log.debug(">>>>>>>>>> Request is {}",request);
        String generatedUuid = UUID.randomUUID().toString();
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
