package me.sitech.apifort.domain.response.cache;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class CacheEndpointRes {
    private String cacheKey;
    private String propHashKey;
    private String regex;
}
