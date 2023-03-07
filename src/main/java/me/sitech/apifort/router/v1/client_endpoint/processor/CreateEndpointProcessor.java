package me.sitech.apifort.router.v1.client_endpoint.processor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.entity.ClientProfileEntity;
import me.sitech.apifort.domain.entity.EndpointPanacheEntity;
import me.sitech.apifort.domain.entity.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.domain.response.endpoints.ServiceEndpointRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.router.v1.client_endpoint.ClientEndpointMapper;
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
    private final CacheClient redisClient;

    public CreateEndpointProcessor(CacheClient redisClient){
        this.redisClient=redisClient;
    }

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        //EXTRACT POST BODY and REALM PATH VARIABLE
        String realm = exchange.getIn().getHeader(ApiFort.API_REALM,String.class);
        PostEndpointReq req = exchange.getIn().getBody(PostEndpointReq.class);

        //FIND CLIENT PROFILE BY REALM
        ClientProfileEntity clientEntity = ClientProfileEntity.findByRealm(realm);
        ServicePanacheEntity serviceEntity = ServicePanacheEntity.findByUuid(req.getServiceUuid());

        Util.verifyAllowedRestMethod(req.isPublicService(),req.getMethodType());
        Util.verifyEndpointPath(req.getEndpointPath());

        EndpointPanacheEntity endpointEntity = ClientEndpointMapper.requestToEntityMapper(req,serviceEntity.getContext(),clientEntity.getUuid());
        if(isEndpointMatchExistRegex(endpointEntity,serviceEntity.getContext())){
            throw new APIFortGeneralException("Endpoint Already exists or match exist regex");
        }
        EndpointPanacheEntity.saveOrUpdate(endpointEntity);
        publishToRedisCache(clientEntity.getApiKey(),serviceEntity.getContext(),endpointEntity);

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(new ServiceEndpointRes(endpointEntity.getUuid()));
    }


    public static boolean isEndpointMatchExistRegex(EndpointPanacheEntity entity, String context){
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
        redisClient.cacheEndpoint(apiKey,
                context,
                endpointEntity.getMethodType(),
                endpointEntity.getEndpointRegex(),new ObjectMapper().writeValueAsString(endpointEntity));
    }
}
