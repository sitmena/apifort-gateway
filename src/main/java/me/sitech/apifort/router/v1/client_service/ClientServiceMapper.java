package me.sitech.apifort.router.v1.client_service;

import me.sitech.apifort.domain.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostClientServiceReq;
import me.sitech.apifort.exceptions.APIFortGeneralException;

public final class ClientServiceMapper {

    private ClientServiceMapper() {
        throw new APIFortGeneralException("Mapper class");
    }

    public static ServicePanacheEntity mappServicePanacheEntity(PostClientServiceReq req,String profileUuid){
        ServicePanacheEntity entity = new ServicePanacheEntity();
        entity.setUuid(req.getUuid());
        entity.setClientProfileUuidFK(profileUuid);
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        entity.setContext(req.getContext());
        entity.setPath(req.getPath());

        return  entity;
    }
}
