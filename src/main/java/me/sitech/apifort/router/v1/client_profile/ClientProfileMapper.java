package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsRes;

import java.util.UUID;

@Slf4j
public class ClientProfileMapper {

    public static ClientProfileDetailsRes mapClientProfileRes(ClientProfilePanacheEntity entity){
        ClientProfileDetailsRes response = new ClientProfileDetailsRes();
        response.setClientProfileUuid(entity.getUuid());
        response.setRealm(entity.getRealm());
        response.setAuthClaimKey(entity.getAuthClaimKey());
        response.setApiKey(entity.getApiKey());
        response.setPublicCertificate(entity.getPublicCertificate());
        return response;
    }

    public static ClientProfilePanacheEntity mapClientProfilePanacheEntity(PostClientProfileReq request) {
        log.debug(">>>>>>>>>> Request is {}", request);
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }

    public static ClientProfilePanacheEntity mapClientProfileEntity(PostClientProfileReq request) {
        log.debug(">>>>>>>>>> Request is {}",request);
        String generatedUuid = UUID.randomUUID().toString();
        ClientProfilePanacheEntity entity = new ClientProfilePanacheEntity();
        entity.setUuid(generatedUuid);
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
