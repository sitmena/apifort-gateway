package me.sitech.apifort.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "client_endpoints")
public class EndpointPanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid",nullable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "clients_profile_uuid", nullable = false,length = 36)
    private String clientProfileFK;

    @Column(name = "service_name", length = 150)
    private String serviceName;

    @Column(name = "context_path", length = 150)
    private String contextPath;

    @Column(name = "endpoint_path",length = 250)
    private String endpointPath;

    @Column(name = "endpoint_regex",length = 250)
    private String endpointRegex;

    @Column(name = "method_type", length = 6)
    private String methodType;

    @Column(name = "auth_claim_value",nullable = false,length = 250)
    private String authClaimValue;

    @Column(name = "offline_authentication")
    private boolean offlineAuthentication;

    @Column(name ="is_public_service")
    private boolean publicEndpoint;

    @Column(name = "version_number",length = 5)
    private Integer versionNumber;

    @Column(name = "is_activate")
    private boolean activated;

    @Column(name = "is_terminate")
    private boolean terminated;

    @ActivateRequestContext
    public static EndpointPanacheEntity findByUuid(String uuid){
        try {
            return find("uuid=?1",uuid).singleResult();
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    @ActivateRequestContext
    public static List<EndpointPanacheEntity> findByClientProfileFK(String clientProfileFK){
        return list("clientProfileFK=?1",clientProfileFK);
    }

    @ActivateRequestContext
    public static List<EndpointPanacheEntity> findByClientProfileFKAndMethodType(String uuid, String methodType){
        return list("clientProfileFK=?1 and methodType=?2",uuid,methodType);
    }

    @Transactional
    public static void save(EndpointPanacheEntity entity) {
        persist(entity);
    }

    @Transactional
    public static void terminate(String apiKey){
        delete("uuid=?1",apiKey);
    }

    @Transactional
    public static void deleteByClientProfileFK(String clientProfileFK){
        delete("clientProfileFK=?1",clientProfileFK);
    }

}
