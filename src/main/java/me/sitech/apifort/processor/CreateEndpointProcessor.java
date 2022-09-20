package me.sitech.apifort.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
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
        PostEndpointReq request = exchange.getIn().getBody(PostEndpointReq.class);

        //FIND CLIENT PROFILE BY REALM
        ClientProfilePanacheEntity clientEntity = ClientProfilePanacheEntity.findByRealm(realm);
        ServicePanacheEntity serviceEntity = ServicePanacheEntity.findByUuid(request.getServiceUuid());

        Util.verifyAllowedRestMethod(request.isPublicService(),request.getMethodType());
        Util.verifyEndpointPath(request.getEndpointPath());

        EndpointPanacheEntity endpointEntity = requestToEntityMapper(request,serviceEntity.getContext(),clientEntity.getUuid());
        if(isEndpointMatchExistRegex(endpointEntity,serviceEntity.getContext(),serviceEntity.getUuid())){
            throw new APIFortGeneralException("Endpoint Already exists or match exist regex");
        }
        EndpointPanacheEntity.save(endpointEntity);
        publishToRedisCache(clientEntity.getApiKey(),serviceEntity.getContext(),endpointEntity);

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(new ClientEndpointRes(endpointEntity.getUuid()));
    }


    private EndpointPanacheEntity requestToEntityMapper(PostEndpointReq req,String contextPath, String clientUuidFK) {
        String endpointCustomRegex = Util.generateApiFortPathRegex(req.isPublicService(),contextPath,req.getEndpointPath());
        EndpointPanacheEntity entity = new EndpointPanacheEntity();
        entity.setClientUuidFk(clientUuidFK);
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        entity.setServiceUuidFk(req.getServiceUuid());
        entity.setEndpointPath(req.getEndpointPath());
        entity.setMethodType(req.getMethodType());
        entity.setOfflineAuthentication(req.isOfflineAuthentication());
        entity.setVersionNumber(req.getVersionNumber());
        entity.setAuthClaimValue(req.getAuthClaimValue());
        entity.setPublicEndpoint(req.isPublicService());
        entity.setEndpointRegex(endpointCustomRegex);
        entity.setActivated(false);
        return entity;
    }

    public static boolean isEndpointMatchExistRegex(EndpointPanacheEntity entity, String context, String serviceUuid){
        String apiFortPath = Util.generateApiFortPath(entity.isPublicEndpoint(),context,entity.getEndpointPath());
        List<EndpointPanacheEntity> results = EndpointPanacheEntity.
                findByServiceUuidFkAndMethodType(entity.getServiceUuidFk(),entity.getMethodType());
        Optional<EndpointPanacheEntity> optionalResults= results.parallelStream().filter(item->{
            final Matcher fullMatcher = Pattern.compile(item.getEndpointRegex()).matcher(apiFortPath);
            return fullMatcher.find();
        }).findFirst();
        return  optionalResults.isPresent();
    }

    public void publishToRedisCache(String apiKey,String context,EndpointPanacheEntity endpointEntity) throws JsonProcessingException {

        redisClient.addProfileEndpoint(apiKey,
                context,
                endpointEntity.getMethodType(),
                endpointEntity.getEndpointRegex(),new ObjectMapper().writeValueAsString(endpointEntity));
    }
}
