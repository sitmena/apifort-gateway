package me.sitech.apifort.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client_endpoints")
public class EndpointPanacheEntity extends PanacheEntity {

    @Column(name = "uuid",nullable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "clients_profile_uuid", nullable = false)
    private String clientProfileFK;

    @Column(name = "service_name", length = 150)
    private String serviceName;

    @Column(name = "context_path", length = 200)
    private String contextPath;

    @Column(name = "endpoint_path")
    private String endpointPath;

    @Column(name = "endpoint_regex")
    private String endpointRegex;

    @Column(name = "method_type", length = 10)
    private String methodType;

    @Column(name = "auth_claim_value",nullable = false)
    private String authClaimValue;

    @Column(name = "offline_authentication")
    private boolean offlineAuthentication;

    @Column(name = "version_number")
    private Integer versionNumber;

    @Column(name = "is_activate")
    private boolean activated;

    @Column(name = "is_terminate")
    private boolean terminated;

    @Transactional
    public void save(EndpointPanacheEntity entity) {
        persist(entity);
    }

    @Transactional
    public static EndpointPanacheEntity findByUuid(String uuid){
        try {
            return find("uuid=?1",uuid).singleResult();
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @ActivateRequestContext
    public static List<EndpointPanacheEntity> findByClientProfileFK(String uuid){
        return list("clientProfileFK=?1",uuid);
    }

    public static List<EndpointPanacheEntity> findByClientProfileFKAndMethodType(String uuid, String methodType){
        return list("clientProfileFK=?1 and methodType=?2",uuid,methodType);
    }


    @Transactional
    public static void terminate(String apiKey){
        delete("uuid=?1",apiKey);
    }
}
