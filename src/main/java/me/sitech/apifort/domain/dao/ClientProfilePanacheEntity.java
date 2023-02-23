package me.sitech.apifort.domain.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.sitech.apifort.exceptions.ApiFortEntityException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "apifort_client_profile",
        indexes = {
        @Index(name = "apifort_client_profile_index_realm", columnList = "realm"),
        @Index(name = "apifort_client_profile_index_apikey", columnList = "api_key")
})
public class ClientProfilePanacheEntity extends PanacheEntityBase {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(name = "uuid",nullable = false,updatable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "api_key",unique = true,length = 36)
    private String apiKey;

    @Column(name = "jwt_public_certificate",length = 3000)
    private String publicCertificate;

    @Column(name = "realm",unique = true,length = 50)
    private String realm;

    @Column(name = "auth_claim_key",nullable = false,length = 60)
    private String authClaimKey;

    @CreationTimestamp
    @Column(name="created_date")
    private LocalDateTime createdDate ;

    @UpdateTimestamp
    @Column(name="updated_date")
    private LocalDateTime updatedDate;

    public static String saveOrUpdate(final ClientProfilePanacheEntity entity) {
        persist(entity);
        return entity.getUuid();
    }

    @ActivateRequestContext
    public static Optional<ClientProfilePanacheEntity> findByUuid(final String uuid){
        return find("uuid=?1",uuid).singleResultOptional();
    }

    @ActivateRequestContext
    public static Optional<ClientProfilePanacheEntity> findByApiKey(final String apiKey){
        return find("apiKey=?1",apiKey).singleResultOptional();
    }

    public static boolean isApiKeyExist(final String apiKey){
        return count("apiKey=?1",apiKey)>0;
    }


    @ActivateRequestContext
    public static ClientProfilePanacheEntity findByRealm(final String realm){
        Optional<ClientProfilePanacheEntity> resultOptional =  find("realm=?1",realm).singleResultOptional();
        if(resultOptional.isEmpty())
            throw new ApiFortEntityException(String.format("%s realm is not exist",realm));
        return resultOptional.get();
    }

    @Transactional
    public static void deleteByUuid(final String profileUuid){
        delete("uuid=?1",profileUuid);
    }
}
