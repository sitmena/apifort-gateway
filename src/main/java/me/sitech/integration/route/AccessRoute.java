package me.sitech.integration.route;

import io.quarkus.grpc.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.router.v1.security.JwtAuthenticationRoute;
import me.sitech.integration.domain.constant.RoutingConstant;
import me.sitech.integration.domain.module.access.key.KeysServiceGrpc;
import me.sitech.integration.domain.module.access.key.PublicKeyReplay;
import me.sitech.integration.domain.module.access.key.PublicKeyRequest;
import me.sitech.integration.exception.IntegrationExceptionHandler;
import org.apache.camel.builder.RouteBuilder;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
public class AccessRoute extends RouteBuilder {

    @GrpcClient
    KeysServiceGrpc.KeysServiceBlockingStub keysService;

    private final IntegrationExceptionHandler exception;

    public AccessRoute(IntegrationExceptionHandler exception) {
        this.exception = exception;
    }

    public static final String REALM_NAME_PARAM = "realmName";
    @Override
    public void configure() throws Exception {

        onException(Exception.class).handled(true).process(exception).marshal().json();

        from(RoutingConstant.DIRECT_ACCESS_GET_KEY_ROUTE)
                .id(RoutingConstant.DIRECT_ACCESS_GET_KEY_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(REALM_NAME_PARAM, String.class);
                            PublicKeyReplay kcResponse = keysService.getPublicKey(PublicKeyRequest.newBuilder().setRealmName(realmName).build());
                            exchange.getIn().setBody(kcResponse.getValue());
                        }
                ).log("Received ${body}").marshal().json();

        from(RoutingConstant.DIRECT_ACCESS_GET_CERTIFICATE_ROUTE)
                .id(RoutingConstant.DIRECT_ACCESS_GET_CERTIFICATE_ROUTE_ID)
                .to(JwtAuthenticationRoute.DIRECT_JWT_AUTH_ROUTE)
                .process(exchange -> {
                            String realmName = exchange.getIn().getHeader(REALM_NAME_PARAM, String.class);
                            PublicKeyReplay kcResponse = keysService.getCertificate(PublicKeyRequest.newBuilder().setRealmName(realmName).build());
                            exchange.getIn().setBody(kcResponse.getValue());
                        }
                ).log("Received ${body}").marshal().json();
    }
}
