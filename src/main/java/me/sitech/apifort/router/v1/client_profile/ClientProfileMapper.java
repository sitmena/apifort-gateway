package me.sitech.apifort.router.v1.client_profile;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.entity.ClientProfileEntity;
import me.sitech.apifort.domain.module.ProfileCounts;
import me.sitech.apifort.domain.request.PostClientProfileReq;
import me.sitech.apifort.domain.response.profile.ClientProfileDetailsRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public final class ClientProfileMapper {

    private ClientProfileMapper() {
        throw new APIFortGeneralException("Mapper class");
    }

    public static List<ClientProfileDetailsRes> mapClientProfileRes(List<ClientProfileEntity> entity, List<ProfileCounts> counts){
        List<ClientProfileDetailsRes> result = new ArrayList<>();
        entity.forEach(item->{
            Optional<ProfileCounts> serviceCountOptional =  counts.stream().filter(index->item.getUuid().equals(index.getUuid())).findFirst();
            serviceCountOptional.ifPresent(profileCounts -> result.add(mapClientProfileRes(item, profileCounts.getServiceCount(), profileCounts.getEndpointCount())));
        });
        return result;
    }

    public static ClientProfileDetailsRes mapClientProfileRes(ClientProfileEntity entity, Long serviceCount,Long endpointCount){
        ClientProfileDetailsRes response = new ClientProfileDetailsRes();
        response.setClientProfileUuid(entity.getUuid());
        response.setTitle(entity.getTitle());
        response.setDescription(entity.getDescription());
        response.setRealm(entity.getRealm());
        response.setAuthClaimKey(entity.getAuthClaimKey());
        response.setApiKey(entity.getApiKey());
        response.setPublicCertificate(entity.getPublicCertificate());
        response.setTotalServices(serviceCount);
        response.setTotalEndpoints(endpointCount);
        return response;
    }

    public static ClientProfileEntity mapClientProfileEntity(PostClientProfileReq request) {
        ClientProfileEntity entity = new ClientProfileEntity();
        entity.setTitle(request.getTitle());
        entity.setDescription(request.getDescription());
        entity.setApiKey(request.getApiKey());
        entity.setAuthClaimKey(request.getAuthClaimKey());
        entity.setRealm(request.getRealm());
        return entity;
    }
}
