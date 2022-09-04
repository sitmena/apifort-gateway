package me.sitech.apifort.processor;

import com.sitech.access.PublicAccessServiceGrpc;
import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import io.quarkus.grpc.GrpcClient;
import io.quarkus.redis.client.RedisClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.ClientProfileRequest;
import me.sitech.apifort.domain.response.profile.ClientProfileResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.UUID;

@Slf4j
@ApplicationScoped
public class ClientProfileProcessor implements Processor {

    @Inject
    private RedisClient redisClient;

    @GrpcClient
    private PublicAccessServiceGrpc.PublicAccessServiceBlockingStub publicAccessService;

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        ClientProfileRequest request = exchange.getIn().getBody(ClientProfileRequest.class);
        log.debug(">>>>>>>>>> Request is {}", request);
        if (request == null)
            throw new APIFortGeneralException("Failed to get post body");
        ClientProfilePanacheEntity result = ClientProfilePanacheEntity.findByApiKey(request.getApiKey());

        if (result != null)
            throw new APIFortGeneralException("Profile Already Exists");

        //GET Certificate from REALM
        PublicKeyReplay KcResponse = publicAccessService.getPublicKey(PublicKeyRequest.newBuilder().setRealmName(request.getRealm()).build());
        String publicCertificate = KcResponse.getValue();

        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);
        entity.setPublicCertificate(publicCertificate);

        ClientProfilePanacheEntity.save(entity);
        redisClient.set(Arrays.asList(entity.getApiKey(), entity.getPublicCertificate()));

        ClientProfileResponse response = new ClientProfileResponse();
        response.setClientProfileUuid(entity.getUuid());
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(response);
    }

    private ClientProfilePanacheEntity clientProfileEntityMapping(ClientProfileRequest request) {
        log.debug(">>>>>>>>>> Request is {}", request);
        String generatedUuid = UUID.randomUUID().toString();
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
