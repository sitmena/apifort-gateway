package me.sitech.apifort.api.v1.portal.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.transaction.Transactional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client_endpoints")
public class ClientEndpointPanacheEntity extends PanacheEntity {

    @Column(name = "uuid",nullable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "clients_profile_uuid", nullable = false)
    private String clientProfileFK;

    @Column(name = "service_name", length = 150)
    private String serviceName;

    @Column(name = "endpoint_path")
    private String endpointPath;

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
    public void save(ClientEndpointPanacheEntity entity) {
        persist(entity);
    }

    @Transactional
    public ClientEndpointPanacheEntity findByUuid(String uuid){
        return find("uuid=?1",uuid).singleResult();
    }

    public ClientEndpointPanacheEntity findByClientProfileFK(String uuid){
        return find("clientProfileFK=?1",uuid).singleResult();
    }
    @Transactional
    public static void terminate(String apiKey){
        delete("uuid=?1",apiKey);
    }
}