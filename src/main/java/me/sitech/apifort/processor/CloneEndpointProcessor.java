package me.sitech.apifort.processor;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.EndpointPanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostCopyEndpointReq;
import me.sitech.apifort.domain.response.common.GeneralRes;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;


@Slf4j
@ApplicationScoped
public class CloneEndpointProcessor implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        PostCopyEndpointReq req = exchange.getIn().getBody(PostCopyEndpointReq.class);
        //Find realm to clone endpoints
        ClientProfilePanacheEntity clonedClientProfile = ClientProfilePanacheEntity.findByRealm(req.getRealm());
        //Extract cloning endpoints
        List<EndpointPanacheEntity> endpoints = EndpointPanacheEntity.findByUuidNotMatchClientProfileUuid(req.getEndpointUuid(), clonedClientProfile.getUuid());
        //Extract Service UUIDs
        Map<String, List<EndpointPanacheEntity>> serviceMap = new LinkedHashMap<>();
        endpoints.forEach(endpoint -> {
            endpoint.setClientUuidFk(clonedClientProfile.getUuid());
            endpoint.setUuid(null);
            if (serviceMap.get(endpoint.getServiceUuidFk())==null) {
                serviceMap.put(endpoint.getServiceUuidFk(), Collections.singletonList(endpoint));
            } else {
                serviceMap.get(endpoint.getServiceUuidFk()).add(endpoint);
            }
        });

        /**
         Extract endpoint Service and Clone Real services
         */
        List<ServicePanacheEntity> cloneServiceList = ServicePanacheEntity.findByClientProfileFK(clonedClientProfile.getUuid());
        List<ServicePanacheEntity> endpointServiceList = ServicePanacheEntity.findByUuid(serviceMap.keySet());
        List<ServicePanacheEntity> newCloneServices = new ArrayList<>();
        /**
         Extract not cloned services;
         */
        endpointServiceList.forEach(oldService->{
            Optional<ServicePanacheEntity> result = cloneServiceList.stream()
                    .filter(newService -> oldService.getContext().equals(newService.getContext())&&
                            oldService.getPath().equals(newService.getPath())).findFirst();
            if(result.isEmpty()){
                newCloneServices.add(oldService.withUuid(null).withClientProfileUuidFK(clonedClientProfile.getUuid()));
            }
        });
        if (!newCloneServices.isEmpty()) {
            cloneServiceList.addAll(ServicePanacheEntity.saveOrUpdate(newCloneServices));
        }

        List<EndpointPanacheEntity> newEndpointList = new ArrayList<>();
        endpointServiceList.parallelStream().forEach(oldService -> {
            Optional<ServicePanacheEntity> result = cloneServiceList.stream().filter(newService -> oldService.getPath().equals(newService.getPath()) &&
                    oldService.getContext().equals(newService.getContext())).findFirst();
            result.ifPresent(servicePanacheEntity -> newEndpointList.addAll(serviceMap.get(oldService.getUuid())
                    .parallelStream().peek(endpoint -> endpoint.setServiceUuidFk(servicePanacheEntity.getUuid())).toList()));
        });
        if (!newEndpointList.isEmpty()) {
            EndpointPanacheEntity.saveOrUpdate(newEndpointList);
        }
        exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
        exchange.getIn().setBody(new GeneralRes(ApiFortStatusCode.OK, "services cloned successfully"));
    }
}
