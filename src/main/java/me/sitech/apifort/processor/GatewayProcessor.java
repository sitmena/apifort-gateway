package me.sitech.apifort.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Slf4j
@ApplicationScoped
public class GatewayProcessor implements Processor {


    @Inject
    private ApiFortCache redisClient;

    @Override
    public void process(Exchange exchange) throws Exception {

        //Get Request headers
        String requestPath = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_PATH_HEADER, String.class);
        String methodType = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_METHOD_HEADER,String.class);
        String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER,String.class);
        String token  = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION,String.class);
        if(apiKey==null || apiKey.isEmpty())
            throw new APIFortGeneralException("API key is missing");



        String jsonString = redisClient.checkEndpointExists(apiKey,Util.getContextPath(requestPath),methodType,requestPath);
        EndpointPanacheEntity endpointPanacheEntity = new ObjectMapper().readValue(jsonString, EndpointPanacheEntity.class);
        if(!endpointPanacheEntity.isPublicEndpoint() && endpointPanacheEntity.getAuthClaimValue()!=null){
            List<String> endpointRoles = Arrays.asList(StringUtils.split(endpointPanacheEntity.getAuthClaimValue(), ","));

            List<String> tokenRoles = Util.extractClaims(token,redisClient.findCertificateByApiKey(apiKey));
            if(tokenRoles==null)
                throw new APIFortGeneralException("Unauthorized request, missing authorization role");
            boolean isRoleAuthorized = tokenRoles.stream().parallel().anyMatch(endpointRoles::contains);
            if(!isRoleAuthorized){
                throw new APIFortGeneralException("Your roles not authorized to access this endpoint");
            }
        }
        exchange.getIn().setHeader("dss-endpoint", Util.downStreamServiceEndpoint(endpointPanacheEntity,requestPath));
    }
}
