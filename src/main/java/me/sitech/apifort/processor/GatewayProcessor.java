package me.sitech.apifort.processor;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.log.Log;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.sitech.apifort.constant.ApiFort.APIFORT_DOWNSTREAM_SERVICE_HEADER;

@Slf4j
@ApplicationScoped
public class GatewayProcessor implements Processor {


    @Inject
    private ApiFortCache redisClient;

    @Override
    public void process(Exchange exchange) throws Exception {
        //Get Request headers
        String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE,String.class);
        String requestPath = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_PATH_HEADER, String.class);
        String methodType = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_METHOD_HEADER, String.class);
        String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER, String.class);
        String token = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION, String.class);
        if (apiKey == null || apiKey.isEmpty())
            throw new APIFortGeneralException("API key is missing");
        String jsonString = redisClient.checkEndpointExists(apiKey, Util.getContextPath(requestPath), methodType, requestPath);
        EndpointPanacheEntity endpointPanacheEntity = new ObjectMapper().readValue(jsonString, EndpointPanacheEntity.class);
        String servicePath = ServicePanacheEntity.findByUuid(endpointPanacheEntity.getServiceUuidFk()).getPath();

        if (!endpointPanacheEntity.isPublicEndpoint() && endpointPanacheEntity.getAuthClaimValue() != null) {
            List<String> endpointRoles = Arrays.asList(StringUtils.split(endpointPanacheEntity.getAuthClaimValue(), ","));
            List<String> tokenRoles = Util.extractClaims(token, redisClient.findCertificateByApiKey(apiKey));
            if (tokenRoles == null)
                throw new APIFortGeneralException("Unauthorized request, missing authorization role");
            boolean isRoleAuthorized = tokenRoles.stream().parallel().anyMatch(endpointRoles::contains);
            if (!isRoleAuthorized) {
                throw new APIFortGeneralException("Your roles not authorized to access this endpoint");
            }
        }
        if(ApiFortMediaType.APPLICATION_URLENCODED.equals(contentType)){
            String body = exchange.getIn().getBody(String.class);
            if(body!=null){
                body = body.replace("{","").replace("}","").replace("&","%26").replace(", ","&").replace(",","");
                log.info(">>>>>>>>>>>>>>> {}",body);
                exchange.getIn().setBody(body);
            }
        }
        exchange.getIn().setHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER, Util.downStreamServiceEndpoint(servicePath, requestPath));
    }
}
