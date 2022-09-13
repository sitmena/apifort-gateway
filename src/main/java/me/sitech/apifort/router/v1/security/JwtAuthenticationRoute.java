package me.sitech.apifort.router.v1.security;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
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

    @ConfigProperty(name = "apifort.admin.clock-skew.seconds")
    public long clockSkew;

    @Inject
    private ExceptionHandlerProcessor exception;

    @Inject
    private ApiFortCache redisClient;

    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(DIRECT_JWT_AUTH_ROUTE)
            .routeId(DIRECT_JWT_AUTH_ROUTE_ID)
            .process(exchange -> {
                String token = exchange.getIn().getHeader("Authorization", String.class);
                if (token == null || token.isEmpty()) {
                    throw new APIFortSecurityException("Missing Authorization header");
                }
                String apiKey = exchange.getIn().getHeader(API_KEY_HEADER, String.class);
                log.debug(">>>> API key is {}", apiKey);
                if(apiKey==null || apiKey.isEmpty())
                    throw new APIFortGeneralException(String.format("%s header is missing",API_KEY_HEADER));
                String certificate = superAdminApiKey.equals(apiKey) ?
                        superAdminCertificate:
                        redisClient.findCertificateByApiKey(apiKey);
                if(certificate==null || certificate.isEmpty())
                    throw new APIFortSecurityException("Failed to load client certificate");
                //Verify JWT token
                Jwts.parserBuilder()
                    .setSigningKey(Util.readStringPublicCertificate(certificate))
                    .setAllowedClockSkewSeconds(clockSkew)
                    .build().parse(token.replaceAll(ApiFort.API_FORT_JWT_TOKEN_PREFIX, ApiFort.API_FORT_EMPTY_STRING));
            });
    }
}
