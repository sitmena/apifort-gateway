package me.sitech.apifort.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import me.sitech.apifort.exceptions.ExceptionProcessor;
import me.sitech.apifort.utility.Util;
import org.apache.camel.builder.RouteBuilder;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static me.sitech.apifort.constant.ApiFort.API_KEY_HEADER;

@Slf4j
@ApplicationScoped
public class JwtAuthenticationRoute extends RouteBuilder {

    public static final String DIRECT_JWT_AUTH_ROUTE = "direct:jwt-auth-route?bridgeErrorHandler=true";
    private static final String DIRECT_JWT_AUTH_ROUTE_ID = "jwt-auth-route-id";

    @ConfigProperty(name = "apifort.admin.api-key")
    public String superAdminApiKey;

    @ConfigProperty(name = "apifort.admin.public-certificate")
    public String superAdminCertificate;

    @Inject
    private ExceptionProcessor exceptionProcessor;

    @Inject
    private RedisClient redisClient;


    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exceptionProcessor).marshal().json();

        from(DIRECT_JWT_AUTH_ROUTE)
                .routeId(DIRECT_JWT_AUTH_ROUTE_ID)
                .process(exchange -> {
                    String token = exchange.getIn().getHeader("Authorization", String.class);
                    if (token == null || token.isEmpty()) {
                        throw new APIFortSecurityException("Missing Authorization header");
                    }
                    //TODO check if super admin or not
                    String apiKey = exchange.getIn().getHeader(API_KEY_HEADER, String.class);
                    log.info("API key is {}", apiKey);
                    String certificate = superAdminApiKey.equals(apiKey) ?
                            superAdminCertificate:
                            redisClient.get(apiKey).toString();
                    Jws<Claims> claims = Jwts.parserBuilder()
                            .setSigningKey(Util.readStringPublicCertificate(certificate))
                            .build()
                            .parseClaimsJws(token.replaceAll(ApiFort.API_FORT_JWT_TOKEN_PREFIX, ApiFort.API_FORT_EMPTY_STRING));

                });
    }
}
