package me.sitech.apifort.dao;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import lombok.*;
import me.sitech.apifort.exceptions.ApiFortEntityException;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import javax.enterprise.context.control.ActivateRequestContext;
import javax.persistence.*;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Setter
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "apifort_client_services",
        uniqueConstraints = @UniqueConstraint(
                name = "apifort_client_services_constraint",
                columnNames={"service_path", "service_context"}),
        indexes = {
                @Index(name = "apifort_client_services_constraint_index", columnList = "client_profile_uuid_fk")
        })
public class ServicePanacheEntity extends PanacheEntityBase {

    @Id
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "UUID")
    @Column(name = "uuid",nullable = false,updatable = false,unique = true,length = 36)
    private String uuid;

    @Column(name = "client_profile_uuid_fk", nullable = false,length = 36)
    private String clientProfileUuidFK;

    @Column(name = "service_title", length = 150)
    private String title;

    @Column(name = "description", length = 200)
    private String description;

    @Column(name = "service_path", length = 150)
    private String Path;

    @Column(name = "service_context", length = 100)
    private String context;

    @CreationTimestamp
    @Column(name="created_date")
    private Date createdDate ;

    @UpdateTimestamp
    @Column(name="updated_date")
    private Date updatedDate;

    @Column(name = "is_activate")
    private boolean activated =true;

    @ActivateRequestContext
    public static ServicePanacheEntity findByUuid(String uuid){
        Optional<ServicePanacheEntity> result =  find("uuid=?1",uuid).singleResultOptional();
        if(result.isEmpty()){
            throw new ApiFortEntityException("invalid service id");
        }
        return result.get();
    }

    @Transactional
    public String saveOrUpdate(ServicePanacheEntity entity){
        if(entity.getUuid()!=null){
            update("title=?1,description=?2,Path=?3,context=?4 where uuid=?5",
                    entity.getTitle(),entity.getDescription(),entity.getPath(),entity.getContext(), entity.getUuid());
            return entity.getUuid();
        }
        persist(entity);
        return entity.getUuid();
    }

    @ActivateRequestContext
    public static List<ServicePanacheEntity> findByClientProfileFK(String clientProfileUuidFK){
        return list("clientProfileUuidFK=?1",clientProfileUuidFK);
    }

}
