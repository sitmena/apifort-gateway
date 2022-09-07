package me.sitech.apifort.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointRequest;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class CreateEndpointProcessor implements Processor {

    @Inject
    private ApiFortCache redisClient;

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        //EXTRACT POST BODY and REALM PATH VARIABLE
        String realm = exchange.getIn().getHeader("realm",String.class);
        PostEndpointRequest request = exchange.getIn().getBody(PostEndpointRequest.class);

        //FIND CLIENT PROFILE BY REALM
        ClientProfilePanacheEntity clientProfileEntity = ClientProfilePanacheEntity.findByRealm(realm);
        if(clientProfileEntity==null)
            throw new APIFortGeneralException("Invalid Realm");

        Util.verifyAllowedRestMethod(request.isPublicService(),request.getMethodType());
        Util.verifyEndpointPath(request.getEndpointPath());

        EndpointPanacheEntity endpointEntity = requestToEntityMapper(request,clientProfileEntity.getUuid());
        if(isEndpointMatchExistRegex(endpointEntity)){
            throw new APIFortGeneralException("Endpoint Already exists or match exist regex");
        }
        EndpointPanacheEntity.save(endpointEntity);
        publishToRedisCache(clientProfileEntity.getApiKey(),endpointEntity);

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(new ClientEndpointResponse(endpointEntity.getUuid()));
    }


    private EndpointPanacheEntity requestToEntityMapper(PostEndpointRequest request, String clientProfileFK) {
        String endpointCustomRegex = Util.generateApiFortPathRegex(request.isPublicService(),request.getContextPath(),request.getEndpointPath());

        String generatedUuid = UUID.randomUUID().toString();
        EndpointPanacheEntity entity = new EndpointPanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setClientProfileFK(clientProfileFK);
        entity.setEndpointPath(request.getEndpointPath());
        entity.setMethodType(request.getMethodType());
        entity.setOfflineAuthentication(request.isOfflineAuthentication());
        entity.setServiceName(request.getServiceName());
        entity.setContextPath(request.getContextPath());
        entity.setVersionNumber(request.getVersionNumber());
        entity.setAuthClaimValue(request.getAuthClaimValue());
        entity.setPublicEndpoint(request.isPublicService());
        entity.setEndpointRegex(endpointCustomRegex);
        entity.setActivated(false);
        entity.setTerminated(false);
        return entity;
    }

    public static boolean isEndpointMatchExistRegex(EndpointPanacheEntity entity){
        String apiFortPath = Util.generateApiFortPath(entity.isPublicEndpoint(),entity.getContextPath(),entity.getEndpointPath());
        List<EndpointPanacheEntity> results = EndpointPanacheEntity.
                findByClientProfileFKAndMethodType(entity.getClientProfileFK(),entity.getMethodType());
        Optional<EndpointPanacheEntity> optionalResults= results.parallelStream().filter(item->{
            final Matcher fullMatcher = Pattern.compile(item.getEndpointRegex()).matcher(apiFortPath);
            return fullMatcher.find();
        }).findFirst();
        return  optionalResults.isPresent();
    }

    public void publishToRedisCache(String apiKey,EndpointPanacheEntity endpointEntity) throws JsonProcessingException {

        redisClient.addProfileEndpoint(apiKey,
                endpointEntity.getContextPath(),
                endpointEntity.getMethodType(),
                endpointEntity.getEndpointRegex(),new ObjectMapper().writeValueAsString(endpointEntity));
    }
}
