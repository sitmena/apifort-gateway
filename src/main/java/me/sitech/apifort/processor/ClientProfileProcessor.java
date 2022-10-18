package me.sitech.apifort.processor;

import com.sitech.access.PublicAccessServiceGrpc;
import com.sitech.access.PublicKeyReplay;
import com.sitech.access.PublicKeyRequest;
import io.quarkus.grpc.GrpcClient;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.config.ApiFortProps;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.domain.response.profile.PostClientProfileRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@Slf4j
@ApplicationScoped
public class ClientProfileProcessor implements Processor {

    @Inject
    private ApiFortProps props;

    @Inject
    private ApiFortCache redisClient;

    @GrpcClient
    private PublicAccessServiceGrpc.PublicAccessServiceBlockingStub publicAccessService;

    @Transactional
    @Override
    public void process(Exchange exchange) throws Exception {
        PostClientProfileReq request = exchange.getIn().getBody(PostClientProfileReq.class);
        log.debug(">>>>>>>>>> Request is {}", request);

        if (request == null)
            throw new APIFortGeneralException("Failed to get post body");
        if (ClientProfilePanacheEntity.isApiKeyExist(request.getApiKey()))
            throw new APIFortGeneralException("Profile Already Exists");
        if(request.getApiKey().equals(props.admin().apikey()))
            throw new APIFortGeneralException("API Key already exist");

        //GET Certificate from REALM
        PublicKeyReplay publicKey = publicAccessService.getPublicKey(PublicKeyRequest.newBuilder().setRealmName(request.getRealm()).build());
        String publicCertificate = publicKey.getValue();

        ClientProfilePanacheEntity entity = clientProfileEntityMapping(request);
        entity.setPublicCertificate(publicCertificate);

        String uuid = ClientProfilePanacheEntity.saveOrUpdate(entity);

        redisClient.addProfileCertificate(entity.getApiKey(),entity.getPublicCertificate(),request.getRealm());

        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(new PostClientProfileRes(uuid));
    }

    private ClientProfilePanacheEntity clientProfileEntityMapping(PostClientProfileReq request) {
        log.debug(">>>>>>>>>> Request is {}", request);
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
