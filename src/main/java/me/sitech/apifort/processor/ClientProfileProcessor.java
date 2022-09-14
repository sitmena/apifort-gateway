package me.sitech.apifort.processor;

import com.sitech.access.PublicAccessServiceGrpc;
import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.PostClientProfileRequest;
import me.sitech.apifort.domain.response.profile.PostClientProfileResponse;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@ApplicationScoped
public class ClientProfileProcessor implements Processor {
    @Inject
    private ApiFortCache redisClient;
    @GrpcClient
    private PublicAccessServiceGrpc.PublicAccessServiceBlockingStub publicAccessService;

    @Override
    @Transactional
    public void process(Exchange exchange) throws Exception {
        PostClientProfileRequest request = exchange.getIn().getBody(PostClientProfileRequest.class);
        log.debug(">>>>>>>>>> Request is {}", request);

        if (request == null)
            throw new APIFortGeneralException("Failed to get post body");
        if (ClientProfilePanacheEntity.isApiKeyExist(request.getApiKey()))
            throw new APIFortGeneralException("Profile Already Exists");

        //GET Certificate from REALM
        PublicKeyReplay publicKey = publicAccessService.getPublicKey(PublicKeyRequest.newBuilder().setRealmName(request.getRealm()).build());
        String publicCertificate = publicKey.getValue();

        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);
        entity.setPublicCertificate(publicCertificate);

        String uuid = entity.saveOrUpdate(entity);

        redisClient.addProfileCertificate(entity.getApiKey(),entity.getPublicCertificate());

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(new PostClientProfileResponse(uuid));
    }

    private ClientProfilePanacheEntity clientProfileEntityMapping(PostClientProfileRequest request) {
        log.debug(">>>>>>>>>> Request is {}", request);
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
