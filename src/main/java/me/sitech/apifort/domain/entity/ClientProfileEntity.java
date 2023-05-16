package me.sitech.apifort.domain.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import me.sitech.apifort.domain.module.ProfileCounts;
import me.sitech.apifort.exceptions.ApiFortEntityException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
public class ClientProfileEntity extends PanacheEntityBase {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(name = "uuid",nullable = false,updatable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "title",length = 100)
    private String title;

    @Column(name = "description",length = 150)
    private String description;

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

    public static String saveOrUpdate(final ClientProfileEntity entity) {
        persist(entity);
        return entity.getUuid();
    }

    @ActivateRequestContext
    public static Optional<ClientProfileEntity> findByUuid(final String uuid){
        return find("uuid=?1",uuid).singleResultOptional();
    }

    @ActivateRequestContext
    public static  List<ProfileCounts> getCounts(){
        String svcCountSql = """
                select t1.uuid, count(t2.uuid) as service_count
                from apifort_client_profile t1
                     left outer join apifort_client_services t2 on t1.uuid = t2.client_profile_uuid_fk
                     group by t1.uuid
                """;
        List<Object[]> svcResult = getEntityManager().createNativeQuery(svcCountSql).getResultList();
        List<ProfileCounts> countsList = new ArrayList<>();
        svcResult.forEach(obj->{countsList.add(new ProfileCounts().mapServiceCount(obj));});

        String endpointCountSql = """
                select t1.uuid, count(t3.uuid) as endpoint_count
                from apifort_client_profile t1
                     left outer join apifort_client_services t2 on t1.uuid = t2.client_profile_uuid_fk
                     left outer join apifort_client_endpoints t3 on t2.uuid = t3.service_uuid_fk
                     group by t1.uuid
                """;
        List<Object[]> endpointResult = getEntityManager().createNativeQuery(endpointCountSql).getResultList();
        for (Object[] record: endpointResult) {
            countsList.stream()
                .filter(countItem -> countItem.getUuid().equals(record[0])).findFirst().ifPresent(profileCount -> {
                    profileCount.mapEndpointCount(record);
                });
        }

        return countsList;
    }

    @ActivateRequestContext
    public static ProfileCounts getCounts(String uuid){
        String svcCountSql = """
                select t1.uuid, count(t2.uuid) as service_count
                from apifort_client_profile t1
                     left outer join apifort_client_services t2 on t1.uuid = t2.client_profile_uuid_fk
                where t1.uuid = :uuid group by t1.uuid
                """;
        Object[] result = (Object[])getEntityManager().createNativeQuery(svcCountSql).setParameter("uuid", uuid).getSingleResult();
        ProfileCounts profileCounts = new ProfileCounts().mapServiceCount(result);

        String endpointCountSql = """
                select t1.uuid, count(t3.uuid) as endpoint_count
                from apifort_client_profile t1
                     left outer join apifort_client_services t2 on t1.uuid = t2.client_profile_uuid_fk
                     left outer join apifort_client_endpoints t3 on t2.uuid = t3.service_uuid_fk
                     where t1.uuid = :uuid
                     group by t1.uuid
                """;
        Object[] endpointsResult = (Object[])getEntityManager().createNativeQuery(endpointCountSql).setParameter("uuid", uuid).getSingleResult();

        return profileCounts.mapEndpointCount(endpointsResult);
    }

    @ActivateRequestContext
    public static Optional<ClientProfileEntity> findByApiKey(final String apiKey){
        return find("apiKey=?1",apiKey).singleResultOptional();
    }

    public static boolean isApiKeyExist(final String apiKey){
        return count("apiKey=?1",apiKey)>0;
    }


    @ActivateRequestContext
    public static ClientProfileEntity findByRealm(final String realm){
        Optional<ClientProfileEntity> resultOptional =  find("realm=?1",realm).singleResultOptional();
        if(resultOptional.isEmpty())
            throw new ApiFortEntityException(String.format("%s realm is not exist",realm));
        return resultOptional.get();
    }

    @Transactional
    public static void deleteByUuid(final String profileUuid){
        delete("uuid=?1",profileUuid);
    }

    @ActivateRequestContext
    public static List<ClientProfileEntity> findAll(String q,int pageIndex, int pageSize){
        if(q!=null){
            String searchQ = "%" + q + "%";
            return find("lower(title) LIKE ?1 OR lower(description) LIKE ?2 ",searchQ,searchQ).page(pageIndex,pageSize).list();
        }
        return findAll().page(pageIndex,pageSize).list();
    }

    @ActivateRequestContext
    public static long totalProfiles(){
        return count();
    }

    @Transactional
    public static void updateProfile(String title, String description, String authClaimKey, String profileUuid,String realm) {
        update("title=?1,description=?2,auth_claim_key=?3 where uuid=?4 and realm=?5",
                title,description,authClaimKey,profileUuid,realm);
    }
}
