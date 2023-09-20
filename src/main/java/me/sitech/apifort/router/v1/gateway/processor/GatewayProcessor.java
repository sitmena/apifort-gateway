package me.sitech.apifort.router.v1.gateway.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.CacheClient;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortMediaType;
import me.sitech.apifort.domain.entity.EndpointPanacheEntity;
import me.sitech.apifort.domain.entity.ServicePanacheEntity;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static me.sitech.apifort.constant.ApiFort.APIFORT_DOWNSTREAM_SERVICE_HEADER;

@Slf4j
@ApplicationScoped
public class GatewayProcessor implements Processor {

    private static final Pattern PATTERN = Pattern.compile("\\{(.*?)}");
    private final CacheClient cacheClient;

    @Inject
    public GatewayProcessor(CacheClient cacheClient){
        this.cacheClient =cacheClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        //Get Request headers
        String contentType = exchange.getIn().getHeader(Exchange.CONTENT_TYPE,String.class);
        String requestPath = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_PATH_HEADER, String.class);
        String methodType = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_METHOD_HEADER, String.class);
        String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER, String.class);
        String token = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER_AUTHORIZATION, String.class);
        if (apiKey == null || apiKey.isEmpty()) {
            throw new APIFortGeneralException(String.format("API key is missing, requestPath[%s], method[%s]",
                    requestPath, methodType));
        }
        String jsonString = cacheClient.searchCacheEndpoint(apiKey, Util.getContextPath(requestPath), methodType, requestPath);
        EndpointPanacheEntity endpointPanacheEntity = new ObjectMapper().readValue(jsonString, EndpointPanacheEntity.class);
        String servicePath = ServicePanacheEntity.findByUuid(endpointPanacheEntity.getServiceUuidFk()).getPath();

        if (!endpointPanacheEntity.isPublicEndpoint() && endpointPanacheEntity.getAuthClaimValue() != null) {
            List<String> endpointRoles = Arrays.asList(StringUtils.split(endpointPanacheEntity.getAuthClaimValue(), ","));
            List<String> tokenRoles = Util.extractClaims(token, cacheClient.findCacheCertificate(apiKey));
            if (tokenRoles == null)
                throw new APIFortGeneralException("Unauthorized request, missing authorization role");
            boolean isRoleAuthorized = tokenRoles.stream().parallel().anyMatch(endpointRoles::contains);
            if (!isRoleAuthorized) {
                throw new APIFortGeneralException("Your roles not authorized to access this endpoint");
            }
        }

        String body = exchange.getIn().getBody(String.class);
        if(body != null && ApiFortMediaType.APPLICATION_URLENCODED.equals(contentType)) {
            StringBuilder sb = new StringBuilder();

            Matcher matcher = PATTERN.matcher(body);
            if(matcher.find()){
                String[] params = matcher.group(1).split(",");
                Arrays.stream(params).forEach(item ->
                    sb.append(item.trim().replace("&","%26")).append("&")
                );
                log.debug(">>>>> {}", sb.delete(sb.length()-1, sb.length()));
                exchange.getIn().setBody(sb.toString());
            }
        } else {
            exchange.getIn().setBody(exchange.getIn().getBody(),String.class);
        }

        exchange.getIn().setHeader(APIFORT_DOWNSTREAM_SERVICE_HEADER, Util.downStreamServiceEndpoint(servicePath, requestPath));
    }
}
