package me.sitech.apifort.router.v1.client_endpoint;

import me.sitech.apifort.domain.entity.EndpointPanacheEntity;
import me.sitech.apifort.domain.request.PostEndpointReq;
import me.sitech.apifort.domain.request.PutEndpointReq;
import me.sitech.apifort.domain.response.endpoints.GetEndpointRes;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import me.sitech.apifort.utility.Util;

import java.util.ArrayList;
import java.util.List;

public final class ClientEndpointMapper {
    private ClientEndpointMapper() {
        throw new APIFortGeneralException("Mapper class");
    }

    public static GetEndpointRes mapGetEndpointRes(EndpointPanacheEntity entity) {
        return new GetEndpointRes()
                .withUuid(entity.getUuid())
                .withServiceUuid(entity.getServiceUuidFk())
                .withTitle(entity.getTitle())
                .withDescription(entity.getDescription())
                .withEndpointPath(entity.getEndpointPath())
                .withMethodType(entity.getMethodType())
                .withAuthClaimValue(entity.getAuthClaimValue())
                .withEndpointRegex(entity.getEndpointRegex())
                .withVersionNumber(entity.getVersionNumber())
                .withOfflineAuthentication(entity.isOfflineAuthentication())
                .withPublicEndpoint(entity.isPublicEndpoint())
                .withCreatedDate(entity.getCreatedDate())
                .withUpdatedDate(entity.getUpdatedDate());
    }

    public static List<GetEndpointRes> mapGetEndpointRes(List<EndpointPanacheEntity> entities) {
        List<GetEndpointRes> endpoints = new ArrayList<>();
        entities.stream().parallel().forEach(item -> endpoints.add(mapGetEndpointRes(item)));
        return endpoints;

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

    public static EndpointPanacheEntity requestToEntityMapper(PutEndpointReq req, String contextPath, String clientUuidFK) {
        String endpointCustomRegex = Util.generateApiFortPathRegex(req.isPublicService(),contextPath,req.getEndpointPath());
        EndpointPanacheEntity entity = new EndpointPanacheEntity();
        entity.setUuid(req.getEndpointUuid());
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
