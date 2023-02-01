package me.sitech.apifort.router.v1.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.config.ApiFortProps;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.exceptions.APIFortSecurityException;
import me.sitech.apifort.processor.ExceptionHandlerProcessor;
import me.sitech.apifort.utility.Util;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static me.sitech.apifort.constant.ApiFort.API_KEY_HEADER;

@Slf4j
@ApplicationScoped
public class JwtAuthenticationRoute extends RouteBuilder {

    public static final String DIRECT_JWT_AUTH_ROUTE = "direct:jwt-auth-route?bridgeErrorHandler=true";
    private static final String DIRECT_JWT_AUTH_ROUTE_ID = "jwt-auth-route-id";

    private final ApiFortProps apiFortProps;
    private final ApiFortCache redisClient;
    private final ExceptionHandlerProcessor exception;

    @Inject
    public JwtAuthenticationRoute(ApiFortProps apiFortProps,ExceptionHandlerProcessor exception, ApiFortCache redisClient) {
        this.apiFortProps=apiFortProps;
        this.exception = exception;
        this.redisClient = redisClient;
    }

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
                    if (apiKey == null || apiKey.isEmpty())
                        throw new APIFortGeneralException(String.format("%s header is missing", API_KEY_HEADER));
                    String certificate = apiFortProps.admin().apikey().equals(apiKey) ?
                            apiFortProps.admin().certificate() :
                            redisClient.findCertificateByApiKey(apiKey);

                    String realm = apiFortProps.admin().apikey().equals(apiKey)?apiFortProps.admin().realm():redisClient.findRealmByApiKey(apiKey);
                    if (certificate == null || certificate.isEmpty())
                        throw new APIFortSecurityException("Failed to load client certificate");

                    log.debug("Realm to be replaced in issuer [{}]", realm);

                    JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder()
                            .setSigningKey(Util.readStringPublicCertificate(certificate))
                            .setAllowedClockSkewSeconds(apiFortProps.admin().clockSkewSeconds());

                    log.debug("Public Certificate => [{}]\nAnd token is => [{}]", certificate,
                            token.replaceAll(ApiFort.API_FORT_JWT_TOKEN_PREFIX, ApiFort.API_FORT_EMPTY_STRING));

                    Jws<Claims> claims = jwtParserBuilder
                            .build()
                            .parseClaimsJws(token.replaceAll(ApiFort.API_FORT_JWT_TOKEN_PREFIX, ApiFort.API_FORT_EMPTY_STRING));

                    String[] issuers = apiFortProps.admin().tokenIssuer().replace("*",realm).split(",");
                    if (Arrays.stream(issuers).filter(s -> s.trim().equalsIgnoreCase(claims.getBody().getIssuer())).count() == 0) {
                        throw new APIFortSecurityException("Invalid issuer");
                    }

//                    String[] audiences = apiFortProps.admin().tokenAud() == null || apiFortProps.admin().tokenAud().isBlank() ?
//                            null : apiFortProps.admin().tokenAud().split(",");
//                    if(Arrays.stream(audiences).filter(s -> s.trim().equalsIgnoreCase(claims.getBody().getAudience())).count() == 0) {
                        //FIXME: Add Audience check per profile not per service.
                        //throw new APIFortSecurityException("Invalid audience");
//                    }

                    exchange.getIn().setHeader(ApiFort.API_REALM_DSS, realm);
                });
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeySpecException {

        /*MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz79jmM+N7c8HRDQLeCgBw8g+oxMrVuBmukRoImUrGdcfPbBcWpDJZ59PIuoluuOOOf5cF7rBti8UWKBwVcBdZqqZnTv3RLdqAvuHxeSM63NGdm2UChp3ywqkcP/yDHkbevuJjlLJIbsuhS+pFLYbVdYT13mdnP2u4xoYLhEYLRHq7Loqr8ninl44Cnx6E3AnCSV/FPdGT0gFutJFHYjKrNfatnBmaz7Uk1LwY+gMUVMbb4Ad0hXEkSO3ZO5Y30fXaquGQrjkMKTQZqIyYbnNaNvGIBnY5t0JnHyfAjASNWic/LpJg2rcSmT9lpVOVzTWWTVXhKW/+oFtHh8n79zNYQIDAQAB]
And token is => [eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjMGRiYzc2Yy0zZTc2LTRhNWEtYmE5MS1mNTM2NmU5Y2M0ZDUifQ.eyJleHAiOjE2NzM3NzA0MjUsImlhdCI6MTY3Mzc2ODYyNSwianRpIjoiZTRhYTkxYjAtZGU4MC00ZWE3LThmYzgtMmZiOTBiOGUwY2VkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9kZWxtb250ZSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZGVsbW9udGUiLCJzdWIiOiIxMzMyNDA4Yy0xM2JhLTQxMmQtOTExZS1hM2U1NmZmMzc5NmUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiZGVsbW9udGUtYXBwIiwic2Vzc2lvbl9zdGF0ZSI6IjI1OGQ5MWI5LTNmYWMtNGY3Zi04M2YxLTcwNTJkZTE3MGJlZSIsInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiIyNThkOTFiOS0zZmFjLTRmN2YtODNmMS03MDUyZGUxNzBiZWUifQ.-UQiKy8LyiABMKfrISLgkFNYG0jxheSs89MvqK25pdk*/
        final String certificate = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAz79jmM+N7c8HRDQLeCgBw8g+oxMrVuBmukRoImUrGdcfPbBcWpDJZ59PIuoluuOOOf5cF7rBti8UWKBwVcBdZqqZnTv3RLdqAvuHxeSM63NGdm2UChp3ywqkcP/yDHkbevuJjlLJIbsuhS+pFLYbVdYT13mdnP2u4xoYLhEYLRHq7Loqr8ninl44Cnx6E3AnCSV/FPdGT0gFutJFHYjKrNfatnBmaz7Uk1LwY+gMUVMbb4Ad0hXEkSO3ZO5Y30fXaquGQrjkMKTQZqIyYbnNaNvGIBnY5t0JnHyfAjASNWic/LpJg2rcSmT9lpVOVzTWWTVXhKW/+oFtHh8n79zNYQIDAQAB";
        JwtParserBuilder jwtParserBuilder = Jwts.parserBuilder()
                .setSigningKey(Util.readStringPublicCertificate(certificate))
                .setAllowedClockSkewSeconds(30);

        final String token = "eyJhbGciOiJIUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJjMGRiYzc2Yy0zZTc2LTRhNWEtYmE5MS1mNTM2NmU5Y2M0ZDUifQ.eyJleHAiOjE2NzM3NzA0MjUsImlhdCI6MTY3Mzc2ODYyNSwianRpIjoiZTRhYTkxYjAtZGU4MC00ZWE3LThmYzgtMmZiOTBiOGUwY2VkIiwiaXNzIjoiaHR0cDovL2xvY2FsaG9zdDo4MTgwL3JlYWxtcy9kZWxtb250ZSIsImF1ZCI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODE4MC9yZWFsbXMvZGVsbW9udGUiLCJzdWIiOiIxMzMyNDA4Yy0xM2JhLTQxMmQtOTExZS1hM2U1NmZmMzc5NmUiLCJ0eXAiOiJSZWZyZXNoIiwiYXpwIjoiZGVsbW9udGUtYXBwIiwic2Vzc2lvbl9zdGF0ZSI6IjI1OGQ5MWI5LTNmYWMtNGY3Zi04M2YxLTcwNTJkZTE3MGJlZSIsInNjb3BlIjoib3BlbmlkIGVtYWlsIHByb2ZpbGUiLCJzaWQiOiIyNThkOTFiOS0zZmFjLTRmN2YtODNmMS03MDUyZGUxNzBiZWUifQ.-UQiKy8LyiABMKfrISLgkFNYG0jxheSs89MvqK25pdk";
        Jws<Claims> claims = jwtParserBuilder
                .build()
                .parseClaimsJws(token.replaceAll(ApiFort.API_FORT_JWT_TOKEN_PREFIX, ApiFort.API_FORT_EMPTY_STRING));

        System.out.println(claims.getBody().getIssuer());
    }
}
