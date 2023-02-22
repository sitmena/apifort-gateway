package me.sitech.apifort.processor;

import me.sitech.apifort.cache.ApiFortCache;
import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostClientServiceReq;
import me.sitech.apifort.domain.response.service.PostServiceRes;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

@ApplicationScoped
public class PostServiceProcessor implements Processor {

    private final ApiFortCache cacheClient;

    @Inject
    public PostServiceProcessor(ApiFortCache cacheClient){
        this.cacheClient = cacheClient;
    }

    @Transactional
    @Override
    public void process(Exchange exchange) throws Exception {
        String realm = exchange.getIn().getHeader("realm",String.class);
        String action = exchange.getIn().getHeader(ApiFort.API_FORT_ROUTER_ACTION,String.class);

        PostClientServiceReq req = exchange.getIn().getBody(PostClientServiceReq.class);
        String profileUuid = ClientProfilePanacheEntity.findByRealm(realm).getUuid();

        ServicePanacheEntity entity = new ServicePanacheEntity();
        entity.setUuid(req.getUuid());
        entity.setClientProfileUuidFK(profileUuid);
        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        entity.setContext(req.getContext());
        entity.setPath(req.getPath());
        String serviceUuid = entity.saveOrUpdate(entity);

        //Save Service details in the cache.
        cacheClient.addCacheService(realm,entity.getContext(),entity.getPath());

        if(action.equals(ApiFort.API_FORT_CREATE_ACTION)){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.CREATED);
            exchange.getIn().setBody(new PostServiceRes(serviceUuid));
        }else if(action.equals(ApiFort.API_FORT_UPDATE_ACTION)){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
            exchange.getIn().setBody(req);
        }
    }
}
