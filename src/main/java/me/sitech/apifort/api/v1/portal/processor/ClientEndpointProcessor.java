package me.sitech.apifort.api.v1.portal.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.api.v1.portal.dao.ClientEndpointPanacheEntity;
import me.sitech.apifort.api.v1.portal.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.api.v1.portal.domain.request.ClientEndpointRequest;
import me.sitech.apifort.api.v1.portal.domain.response.ClientEndpointResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.digest.DigestUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
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

        //TODO Check if URL contains Dynamic path variable
        String regex  = request.getEndpointPath().replaceAll("\\{.*}" ,".*");
        log.info("Path regex is {}", regex);

        ClientEndpointPanacheEntity entity = clientProfileEntityMapping(request);
        List<ClientEndpointPanacheEntity> results = ClientEndpointPanacheEntity.findByClientProfileFK(entity.getClientProfileFK());
        Optional<ClientEndpointPanacheEntity> optionalResults = results.parallelStream().filter(item->{
            log.info("item is {}", item.getEndpointPath());
            return (item.getEndpointPath().equals(entity.getEndpointPath())&&item.getMethodType().equals(entity.getMethodType()));
        }).findFirst();
        log.info("Filter result is {}", optionalResults);
        if(optionalResults.isPresent()){
            throw new APIFortGeneralException("Endpoint Already exists");
        }
        entity.setEndpointRegex(regex);
        entity.save(entity);
        ClientProfilePanacheEntity clientProfilePanacheEntity = ClientProfilePanacheEntity.findByUuid(request.getClientProfileFK());
        log.info("Client Profile details : {}",clientProfilePanacheEntity);
        publishToRedisCache(clientProfilePanacheEntity.getApiKey(),entity);
        exchange.getIn().setBody(new ClientEndpointResponse(entity.getUuid()));
    }


    private ClientEndpointPanacheEntity clientProfileEntityMapping(ClientEndpointRequest request) {
        String generatedUuid = UUID.randomUUID().toString();

        if(!request.getEndpointPath().startsWith("/")){
            request.setEndpointPath("/".concat(request.getEndpointPath()));
        }
        ClientEndpointPanacheEntity entity = new ClientEndpointPanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setClientProfileFK(request.getClientProfileFK());
        entity.setEndpointPath(request.getEndpointPath().trim());
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
        //Check if item already exist on Database and Catching Server
        redisClient.lpush(Arrays.asList(String.format("%s-%s",entity.getMethodType().toUpperCase(),apiKey),entity.getEndpointRegex()));
        redisClient.publish(apiKey,new ObjectMapper().writeValueAsString(entity));
        redisClient.set(Arrays.asList(new DigestUtils("SHA-1").digestAsHex(entity.getMethodType()+entity.getEndpointPath())
                ,new ObjectMapper().writeValueAsString(entity)));
    }
}
