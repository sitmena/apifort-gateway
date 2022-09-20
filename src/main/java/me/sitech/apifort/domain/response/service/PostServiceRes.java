package me.sitech.apifort.domain.response.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostServiceRes {

    @JsonProperty("service_uuid")
    private String serviceUuid;
}
