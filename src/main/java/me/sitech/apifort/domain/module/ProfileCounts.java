package me.sitech.apifort.domain.module;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileCounts {

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "service_count")
    private Long serviceCount;

    @Column(name = "endpoint_count")
    private Long endpointCount;


    public ProfileCounts mapServiceCount(Object[] obj){
        this.uuid = (String) obj[0];
        this.serviceCount = Long.valueOf(obj[1].toString());
        return this;
    }

    public ProfileCounts mapEndpointCount(Object[] obj){
        this.uuid = (String) obj[0];
        this.endpointCount = Long.valueOf(obj[1].toString());
        return this;
    }
}
