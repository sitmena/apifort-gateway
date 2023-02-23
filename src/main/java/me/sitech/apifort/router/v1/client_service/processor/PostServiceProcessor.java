package me.sitech.apifort.router.v1.client_service.processor;

import me.sitech.apifort.constant.ApiFort;
import me.sitech.apifort.constant.ApiFortStatusCode;
import me.sitech.apifort.domain.dao.ClientProfilePanacheEntity;
import me.sitech.apifort.domain.dao.ServicePanacheEntity;
import me.sitech.apifort.domain.request.PostClientServiceReq;
import me.sitech.apifort.domain.response.service.PostServiceRes;
import me.sitech.apifort.router.v1.client_service.ClientServiceMapper;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class PostServiceProcessor implements Processor {

    @Transactional
    @Override
    public void process(Exchange exchange) throws Exception {
        String realm = exchange.getIn().getHeader("realm",String.class);
        String action = exchange.getIn().getHeader(ApiFort.API_FORT_ROUTER_ACTION,String.class);

        PostClientServiceReq req = exchange.getIn().getBody(PostClientServiceReq.class);
        String profileUuid = ClientProfilePanacheEntity.findByRealm(realm).getUuid();

        String serviceUuid = ServicePanacheEntity.saveOrUpdate(ClientServiceMapper.mappServicePanacheEntity(req,profileUuid));

        if(action.equals(ApiFort.API_FORT_CREATE_ACTION)){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.CREATED);
            exchange.getIn().setBody(new PostServiceRes(serviceUuid));
        }else if(action.equals(ApiFort.API_FORT_UPDATE_ACTION)){
            exchange.getIn().setHeader(Exchange.HTTP_RESPONSE_CODE, ApiFortStatusCode.OK);
            exchange.getIn().setBody(req);
        }
    }
}
