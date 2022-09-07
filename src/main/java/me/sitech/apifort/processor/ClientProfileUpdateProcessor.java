package me.sitech.apifort.processor;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.ClientProfileRequest;
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
        ClientProfileRequest request = exchange.getIn().getBody(ClientProfileRequest.class);
        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);
        redisClient.addProfileCertificate(entity.getApiKey(),entity.getPublicCertificate());
    }


    private ClientProfilePanacheEntity clientProfileEntityMapping(ClientProfileRequest request) {
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
