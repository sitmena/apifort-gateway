package me.sitech.apifort.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointRequest;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class CreateEndpointProcessor implements Processor {



    @Inject
    private RedisClient redisClient;

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {

        PostEndpointRequest request = exchange.getIn().getBody(PostEndpointRequest.class);

        //Clean Request Path and context
        request.setContextPath(request.getContextPath().replace("/","").trim());
        String endPoint = request.getEndpointPath().trim();
        endPoint = endPoint.startsWith("/")?endPoint:"/".concat(endPoint);
        request.setEndpointPath(endPoint);

        if(request.isPublicEndpoint()){
            ApiFort.allowedPublicMethods.stream().filter(method-> method.equalsIgnoreCase(request.getMethodType())).findAny().orElseThrow(()->{
                throw new APIFortGeneralException(String.format("%s method is not allowed for public endpoints",request.getMethodType()));
            });
        }else{
            ApiFort.allowedPrivateMethods.stream().filter(method-> method.equalsIgnoreCase(request.getMethodType())).findAny().orElseThrow(()->{
                throw new APIFortGeneralException(String.format("%s method is not allowed for private endpoints",request.getMethodType()));
            });
        }

        //Define Endpoint Regex
        String regexPath = String.format("^/%s/%s%s$",request.isPublicEndpoint()?"guest":"api",request.getContextPath(),Util.getRegex(request.getEndpointPath()));
        String validationPath = String.format("/%s/%s%s",request.isPublicEndpoint()?"guest":"api",request.getContextPath(),request.getEndpointPath());
        log.info("Path regex is {}", regexPath);

        //Check if regex exists and path match exist regex
        List<EndpointPanacheEntity> results = EndpointPanacheEntity.findByClientProfileFKAndMethodType(request.getClientProfileFK(),request.getMethodType());
        Optional<EndpointPanacheEntity> optionalResults= results.parallelStream().filter(item->{
            final Matcher fullMatcher = Pattern.compile(item.getEndpointRegex()).matcher(validationPath);
            return fullMatcher.find();
        }).findFirst();

        if(optionalResults.isPresent()){
            log.info("Find match endpoint is {}", optionalResults.get());
            throw new APIFortGeneralException("Endpoint Already exists or match exist regex");
        }

        EndpointPanacheEntity endpointPanacheEntity = toEntityMapper(request);
        endpointPanacheEntity.setEndpointRegex(regexPath);
        endpointPanacheEntity.save(endpointPanacheEntity);
        ClientProfilePanacheEntity clientProfilePanacheEntity = ClientProfilePanacheEntity.findByUuid(request.getClientProfileFK());

        publishToRedisCache(clientProfilePanacheEntity,endpointPanacheEntity);
        exchange.getIn().setBody(new ClientEndpointResponse(endpointPanacheEntity.getUuid()));
    }


    private EndpointPanacheEntity toEntityMapper(PostEndpointRequest request) {
        String generatedUuid = UUID.randomUUID().toString();
        EndpointPanacheEntity entity = new EndpointPanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setClientProfileFK(request.getClientProfileFK().trim());
        entity.setEndpointPath(request.getEndpointPath().trim());
        entity.setMethodType(request.getMethodType().trim());
        entity.setOfflineAuthentication(request.isOfflineAuthentication());
        entity.setServiceName(request.getServiceName().trim());
        entity.setContextPath(request.getContextPath().trim());
        entity.setVersionNumber(request.getVersionNumber());
        entity.setAuthClaimValue(request.getAuthClaimValue().trim());
        entity.setActivated(false);
        entity.setTerminated(false);
        return entity;
    }

    public void publishToRedisCache(ClientProfilePanacheEntity clientProfileEntity, EndpointPanacheEntity endpointEntity) throws JsonProcessingException {
        //Check if item already exist on Database and Catching Server
        //api-key + context + METHOD

        String endpointCacheGroupId = Util.redisEndpointGroupCacheId(
                clientProfileEntity.getApiKey()
                ,endpointEntity.getContextPath(),endpointEntity.getMethodType());

        String endpointUniqueId =Util.regexEndpointUniqueCacheId(clientProfileEntity.getApiKey()
                ,endpointEntity.getContextPath(),endpointEntity.getMethodType(),endpointEntity.getEndpointRegex());

        redisClient.lpush(Arrays.asList(endpointCacheGroupId,endpointEntity.getEndpointRegex()));
        //Cache Profile
        redisClient.publish(clientProfileEntity.getApiKey(),new ObjectMapper().writeValueAsString(clientProfileEntity));
        //Cache endpoint content
        redisClient.set(Arrays.asList(new DigestUtils("SHA-1").digestAsHex(endpointUniqueId)
                ,new ObjectMapper().writeValueAsString(endpointEntity)));
    }
}
