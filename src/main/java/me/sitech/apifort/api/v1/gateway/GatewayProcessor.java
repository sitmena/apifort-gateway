package me.sitech.apifort.api.v1.gateway;

import io.quarkus.redis.client.RedisClient;
import io.vertx.redis.client.Response;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Objects;
import java.util.Optional;

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
        Response urls = redisClient.lrange(String.format("%s-%s",methodType.toUpperCase(),apiKey),"0","-1");
        Optional<Response> response = urls.stream()
                .peek(num -> log.info("Total GET endpoint:({})",num))
                .filter(url -> Objects.equals(url.toString(), requestPath)).findFirst();
        if (response.isPresent()) {
            exchange.getIn().setHeader("dss-endpoint", String.format("localhost:9000%s",requestPath));
        }else{
            throw new APIFortGeneralException("Invalid path");
        }
    }
}
