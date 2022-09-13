package me.sitech.apifort.domain.response.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

import java.util.ArrayList;
import java.util.List;


@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class CacheRes {

    private String apiKey;
    private String certificate;
    private List<CacheEndpointRes> cacheEndpoints = new ArrayList<>();
}
