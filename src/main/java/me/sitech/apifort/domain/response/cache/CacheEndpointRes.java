package me.sitech.apifort.domain.response.cache;

import lombok.*;

@Data
@With
@AllArgsConstructor
@NoArgsConstructor
public class CacheEndpointRes {
    private String cacheKey;
    private String propHashKey;
    private String regex;
}
