package me.sitech.apifort.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.*;
import javax.transaction.Transactional;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "client_profile")
public class ClientProfilePanacheEntity extends PanacheEntityBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "uuid",nullable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "api_key",unique = true,length = 36)
    private String apiKey;

    @Column(name = "jwt_public_certificate",length = 3000)
    private String publicCertificate;

    @Column(name = "realm",unique = true,length = 50)
    private String realm;

    @Column(name = "auth_claim_key",nullable = false,length = 60)
    private String authClaimKey;

    @Transactional
    public static void save(ClientProfilePanacheEntity entity) {
        persist(entity);
    }

    @ActivateRequestContext
    public static ClientProfilePanacheEntity findByUuid(String uuid){
        return find("uuid=?1",uuid).singleResult();
    }

    @ActivateRequestContext
    public static ClientProfilePanacheEntity findByApiKey(String apiKey){
        return find("apiKey=?1",apiKey).firstResult();
    }

    @ActivateRequestContext
    public static ClientProfilePanacheEntity findByRealm(String realm){
        return find("realm=?1",realm).firstResult();
    }

    @Transactional
    public static void terminate(String profileUuid){
        delete("uuid=?1",profileUuid);
    }
}
