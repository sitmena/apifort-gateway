package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.domain.dao.EndpointPanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.domain.response.endpoints.ClientEndpointDetailsRes;
import me.sitech.apifort.utility.Util;

public class ClientEndpointMapper {

    public static ClientEndpointDetailsRes entityToResponseMapper(EndpointPanacheEntity entity) {
        return new ClientEndpointDetailsRes()
                .withUuid(entity.getUuid())
                .withEndpointPath(entity.getEndpointPath())
                .withMethodType(entity.getMethodType())
                .withAuthClaimValue(entity.getAuthClaimValue())
                .withEndpointRegex(entity.getEndpointRegex())
                .withVersionNumber(entity.getVersionNumber())
                .withOfflineAuthentication(entity.isOfflineAuthentication())
                .withCreatedDate(entity.getCreatedDate())
                .withUpdatedDate(entity.getUpdatedDate());
    }

    public static EndpointPanacheEntity requestToEntityMapper(PostEndpointReq req, String contextPath, String clientUuidFK) {
        String endpointCustomRegex = Util.generateApiFortPathRegex(req.isPublicService(),contextPath,req.getEndpointPath());
        EndpointPanacheEntity entity = new EndpointPanacheEntity();
        entity.setClientUuidFk(clientUuidFK);
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        entity.setServiceUuidFk(req.getServiceUuid());
        entity.setEndpointPath(req.getEndpointPath());
        entity.setMethodType(req.getMethodType());
        entity.setOfflineAuthentication(req.isOfflineAuthentication());
        entity.setVersionNumber(req.getVersionNumber());
        entity.setAuthClaimValue(req.getAuthClaimValue());
        entity.setPublicEndpoint(req.isPublicService());
        entity.setEndpointRegex(endpointCustomRegex);
        entity.setActivated(false);
        return entity;
    }
}
