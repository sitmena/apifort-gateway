package me.sitech.apifort.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.dao.ClientEndpointPanacheEntity;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.digest.DigestUtils;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
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
        String requestPath = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_PATH_HEADER, String.class).replace("/api","");
        String methodType = exchange.getIn().getHeader(ApiFort.CAMEL_HTTP_METHOD_HEADER,String.class);
        String apiKey = exchange.getIn().getHeader(ApiFort.API_KEY_HEADER,String.class);

        //TODO Check if the path has path variable or not using regex for dynamic keys

        log.info(">>>> Path is {}",requestPath);
        //Verify and route request
        Response regexList = redisClient.lrange(String.format("%s-%s",methodType.toUpperCase(),apiKey),"0","-1");
        Optional<Response> response = regexList.stream().parallel()
                .filter(regex -> {
                    log.info("Path :  {}",requestPath);
                    log.info("Regex:  {}",regex.toString());
                    final Matcher fullMatcher = Pattern.compile(regex.toString()).matcher(requestPath);
                    return fullMatcher.find();
                }).findFirst();
        if (response.isPresent()) {
            String entityString = redisClient.get(new DigestUtils("SHA-1").digestAsHex(methodType+response.get())).toString();
            ClientEndpointPanacheEntity entity = new ObjectMapper().readValue(entityString, ClientEndpointPanacheEntity.class);
            List<String> tokenRoles = exchange.getIn().getHeader(ApiFort.API_TOKEN_ROLES,List.class);
            log.info(">>>>>>>>> UserRoles {} TokenRoles {}", entity.getAuthClaimValue(),tokenRoles);
            exchange.getIn().setHeader("dss-endpoint", String.format("%s%s",entity.getServiceName(),requestPath));
        }else{
            throw new APIFortGeneralException("Invalid path");
        }
    }
}
