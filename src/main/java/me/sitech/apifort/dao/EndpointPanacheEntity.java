package me.sitech.apifort.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.exceptions.APIFortGeneralException;
import org.hibernate.annotations.*;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
@Entity
@Table(name = "apifort_client_endpoints",
        indexes = {
                @Index(name = "client_endpoints_client_profile_fk_index", columnList = "client_uuid_fk")})
public class EndpointPanacheEntity extends PanacheEntityBase {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(name = "uuid",nullable = false,updatable = false,unique = true,length = 36)
    private String uuid;

    @Column(name="client_uuid_fk")
    private String clientUuidFk;

    @Column(name="service_uuid_fk")
    private String serviceUuidFk;

    @Column(name="title",length = 150)
    private String title;

    @Column(name="description",length = 200)
    private String description;

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

    @CreationTimestamp
    @Column(name="created_date")
    private Date createdDate ;

    @UpdateTimestamp
    @Column(name="updated_date")
    private Date updatedDate;


    @ActivateRequestContext
    public static EndpointPanacheEntity findByUuid(String uuid){

        Optional<EndpointPanacheEntity> result = find("uuid=?1",uuid).singleResultOptional();
        if(result.isEmpty()){
            throw new APIFortGeneralException("Record not exist");
        }
        return result.get();
    }

    @ActivateRequestContext
    public static List<EndpointPanacheEntity> findByClientProfileFK(String clientUuidFk){
        return list("clientUuidFk=?1",clientUuidFk);
    }

    @ActivateRequestContext
    public static List<EndpointPanacheEntity> findByServiceUuidFk(String serviceUuidFk){
        return list("serviceUuidFk=?1",serviceUuidFk);
    }

    @ActivateRequestContext
    public static List<EndpointPanacheEntity> findByServiceUuidFkAndMethodType(String serviceUuidFk, String methodType){
        return list("serviceUuidFk=?1 and methodType=?2",serviceUuidFk,methodType);
    }

    @Transactional
    public static void save(EndpointPanacheEntity entity) {
        persist(entity);
    }

    @Transactional
    public static void terminate(String uuid){
        delete("uuid=?1",uuid);
    }

    @Transactional
    public static void deleteByClientProfileUuidFK(String clientUuidFk){
        delete("clientUuidFk=?1",clientUuidFk);
    }

}
