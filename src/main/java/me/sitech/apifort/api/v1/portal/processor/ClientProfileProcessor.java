package me.sitech.apifort.api.v1.portal.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.portal.domain.request.ClientProfileRequest;
import me.sitech.apifort.api.v1.portal.domain.response.ClientProfileResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ClientProfileProcessor implements Processor {

    @Inject
    private RedisClient redisClient;

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        ClientProfileRequest request = exchange.getIn().getBody(ClientProfileRequest.class);

        log.info(">>>>>>>>>> Request is {}",request);
        if(request==null)
            throw new APIFortGeneralException("Failed to get post body");
        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);
        ClientProfilePanacheEntity.save(entity);
        publishToRedisCache(entity.getApiKey(),entity.getPublicCertificate());
        ClientProfileResponse response = new ClientProfileResponse();
        response.setRealm(request.getRealm());
        response.setAuthClaimKey(request.getAuthClaimKey());
        response.setClientExternalId(entity.getUuid());
        exchange.getIn().setBody(response);
    }

    private ClientProfilePanacheEntity clientProfileEntityMapping(ClientProfileRequest request) {
        log.info(">>>>>>>>>> Request is {}",request);
        String generatedUuid = UUID.randomUUID().toString();
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setApiKey(request.getApiKey());
        entity.setClientExternalId(request.getClientExternalId());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        entity.setPublicCertificate(Util.unescape(request.getPublicCertificate()));
        return entity;
    }

    public void publishToRedisCache(String apiKey, String publicCertificate){
        log.info("Key: {}\nValue:{}",apiKey,publicCertificate);
        redisClient.set(Arrays.asList(apiKey, Util.unescape(publicCertificate)));
    }
}
