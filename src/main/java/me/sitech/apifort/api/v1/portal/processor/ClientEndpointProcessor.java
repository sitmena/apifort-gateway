package me.sitech.apifort.api.v1.portal.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.portal.dao.ClientEndpointPanacheEntity;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.portal.domain.request.ClientEndpointRequest;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ClientEndpointProcessor implements Processor {

    @Inject
    private RedisClient redisClient;


    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        ClientEndpointRequest request = exchange.getIn().getBody(ClientEndpointRequest.class);
        ClientEndpointPanacheEntity entity = clientProfileEntityMapping(request);
        entity.save(entity);
        ClientProfilePanacheEntity clientProfilePanacheEntity = ClientProfilePanacheEntity.findByUuid(request.getClientProfileFK());
        log.info("Client Profile details : {}",clientProfilePanacheEntity);
        publishToRedisCache(clientProfilePanacheEntity.getApiKey(),entity);
        exchange.getIn().setBody(entity.findByUuid(entity.getUuid()));
    }


    private ClientEndpointPanacheEntity clientProfileEntityMapping(ClientEndpointRequest request) {
        String generatedUuid = UUID.randomUUID().toString();
        ClientEndpointPanacheEntity entity = new ClientEndpointPanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setClientProfileFK(request.getClientProfileFK());
        entity.setEndpointPath(request.getEndpointPath());
        entity.setMethodType(request.getMethodType());
        entity.setOfflineAuthentication(request.isOfflineAuthentication());
        entity.setServiceName(request.getServiceName());
        entity.setVersionNumber(request.getVersionNumber());
        entity.setAuthClaimValue(request.getAuthClaimValue());
        entity.setActivated(false);
        entity.setTerminated(false);
        return entity;
    }

    public void publishToRedisCache(String apiKey, ClientEndpointPanacheEntity entity) throws JsonProcessingException {
        redisClient.lpush(Arrays.asList(String.format("%s-%s",entity.getMethodType().toUpperCase(),apiKey),entity.getEndpointPath()));
        redisClient.publish(apiKey,new ObjectMapper().writeValueAsString(entity));
    }
}
