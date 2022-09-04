package me.sitech.apifort.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class GatewayProcessor implements Processor {


    @Inject
    private RedisClient redisClient;

    @Override
    public void process(Exchange exchange) throws Exception {

        //Get Request headers
        String requestPath = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_PATH_HEADER, String.class);
        String methodType = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_METHOD_HEADER,String.class);
        String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER,String.class);

        if(apiKey==null || apiKey.isEmpty())
            throw new APIFortGeneralException("API key is missing");
        List<?> tokenRoles = exchange.getIn().getHeader(ApiFort.API_TOKEN_ROLES,List.class);

        //TODO Check if the path has path variable or not using regex for dynamic keys

        log.debug(">>>> Path is {}",requestPath);
        log.debug(">>>> user roles is {}",tokenRoles);

        //Verify and route request
        String cacheKey = Util.redisEndpointGroupCacheId(apiKey,Util.getContextPath(requestPath),methodType);

        Response regexList = redisClient.lrange(cacheKey,"0","-1");
        Optional<Response> response = regexList.stream().parallel()
                .filter(regex -> {
                    log.info("Path :  {}",requestPath);
                    log.info("Regex:  {}",regex.toString());
                    final Matcher fullMatcher = Pattern.compile(regex.toString()).matcher(requestPath);
                    return fullMatcher.find();
                }).findFirst();
        if (response.isPresent()) {
            String regexUniqueId = Util.regexEndpointUniqueCacheId(apiKey,Util.getContextPath(requestPath),methodType,String.valueOf(response.get()));
            log.debug(">>>>> Cache key is {}", regexUniqueId);
            String hashKey = Util.getSHA1(regexUniqueId);
            log.debug(">>>>> Cache key is {}", hashKey);
            String entityString = redisClient.get(hashKey).toString();
            EndpointPanacheEntity cacheEntity = new ObjectMapper().readValue(entityString, EndpointPanacheEntity.class);
            log.debug(">>>>>>>>> UserRoles {} TokenRoles {}", cacheEntity.getAuthClaimValue(),tokenRoles);

            if(!cacheEntity.isPublicEndpoint() && cacheEntity.getAuthClaimValue()!=null){
                List<String> endpointRoles = Arrays.asList(StringUtils.split(cacheEntity.getAuthClaimValue(), ","));
                if(tokenRoles==null)
                    throw new APIFortGeneralException("Unauthorized request, missing authorization role");
                boolean isRoleAuthorized = tokenRoles.stream().parallel().anyMatch(endpointRoles::contains);
                if(!isRoleAuthorized){
                    throw new APIFortGeneralException("Your roles not authorized to access this endpoint");
                }
            }

            exchange.getIn().setHeader("dss-endpoint", Util.downStreamServiceEndpoint(cacheEntity,requestPath));
        }else{
            throw new APIFortGeneralException("Invalid path");
        }
    }
}
