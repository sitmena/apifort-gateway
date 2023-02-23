package me.sitech.apifort.cache;

import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.response.cache.CacheEndpointRes;
import me.sitech.apifort.exceptions.ApiFortInvalidEndpoint;
import me.sitech.apifort.utility.Util;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class CacheClient {

    @ConfigProperty(name = "apifort.cache.version")
    public String cacheVersion;

    private final RedisCache redisCache;

    private static final String API_FORT_PUBLIC_CERTIFICATES = "apifort-public-certificate";
    private static final String API_FORT_REALM = "apifort-realm";
    private static final String API_FORT_PROFILE_ENDPOINT_FORMAT = "%s-%s";
    private static final String API_FORT_CONTEXT_METHODS_FORMAT = "%s-%s-%s";
    private static final String API_FORT_CONTEXT_FORMAT = "%s-%s*";

    @Inject
    public CacheClient(RedisCache redisCache) {
        this.redisCache =redisCache;
    }

    //PROFILE
    public String findCacheCertificate(String apiKey){
        return redisCache.pullByKey(API_FORT_PUBLIC_CERTIFICATES,apiKey);
    }

    public String findCacheRealm(String apiKey){
        return redisCache.pullByKey(API_FORT_REALM,apiKey);
    }

    public void cachePublicCertificate(String apiKey, String certificate, String realm){
        redisCache.pushToGroupList(API_FORT_PUBLIC_CERTIFICATES,apiKey,certificate);
        redisCache.pushToGroupList(API_FORT_REALM,apiKey,realm);
    }

    public void removeCacheProfile(String apiKey){
        redisCache.deleteGroupChildKey(API_FORT_PUBLIC_CERTIFICATES,apiKey);
        redisCache.deleteGroupChildKey(API_FORT_REALM,apiKey);
    }

    //ENDPOINTS
    public CacheEndpointRes cacheEndpoint(String apiKey, String context, String method, String urlRegex, String json ) {
        String key = String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),method.toUpperCase());
        if(!redisCache.isExist(key)){
            redisCache.pushToList(key,urlRegex);
            return new CacheEndpointRes(key, cacheEndpointInfo(apiKey,context,method,urlRegex,json),urlRegex);
        }

        Optional<String> result = redisCache.pullListValues(key).parallelStream().filter(urlRegex::equals).findFirst();
        if(result.isEmpty()){
            redisCache.pushToList(key,urlRegex);
            return new CacheEndpointRes(key, cacheEndpointInfo(apiKey,context,method,urlRegex,json),urlRegex);
        }
        //return new CacheEndpointRes(key, String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase()),urlRegex);
        return new CacheEndpointRes(key, cacheEndpointInfo(apiKey,context,method,urlRegex,json),urlRegex);
    }

    public void removeCacheEndpoint(String apiKey, String context, String method, String regex){
        redisCache.removeFromList(String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),
                method.toUpperCase()),regex);
        removeCacheEndpointInfo(apiKey,context,method,regex);
    }

    public String searchCacheEndpoint(String apiKey, String context, String method, String path){
        String key = String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),method.toUpperCase());
        if(!redisCache.isExist(key)){
            throw new ApiFortInvalidEndpoint(String.format("%s not found",path));
        }
        Optional<String> result = redisCache.pullListValues(key)
                .parallelStream().filter(regex -> {
                    final Matcher fullMatcher = Pattern.compile(regex).matcher(path);
                    return fullMatcher.find();
                }).findFirst();
        if(result.isEmpty())
            throw new ApiFortInvalidEndpoint(String.format("%s not found",path));
        return findCacheEndpointInfo(apiKey,context,method,result.get());
    }

    //ENDPOINT PROPERTIES
    private String cacheEndpointInfo(String apiKey, String context, String method, String urlRegex, String json){
        String hashKey = String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase());
        redisCache.pushToGroupList(hashKey,Util.getSHA1(String.format("%s-%s",method,urlRegex)),json);
        return hashKey;
    }

    private String findCacheEndpointInfo(String apiKey, String context, String method, String urlRegex){
        return redisCache.pullByKey(String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase()),
                Util.getSHA1(String.format("%s-%s",method,urlRegex)));
    }

    private void removeCacheEndpointInfo(String apiKey, String context, String method, String urlRegex){
        String key = String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase());
        String sha1 = Util.getSHA1(String.format("%s-%s",method,urlRegex));
        if(redisCache.isExistInGroupList(key,sha1)){
            redisCache.deleteGroupChildKey(key,sha1);
        }
    }

    public void RemoveCacheByApiKey(String apiKey){
        List<String> keys =  redisCache.findPatternKeys(String.format("%s*",apiKey));
        if(keys == null || keys.isEmpty()) {
            return;
        }
        redisCache.remove(keys.toArray(new String[0]));
    }

    public void removeCache(String apiKey, String context){
        List<String> keys =  redisCache.findPatternKeys(String.format(API_FORT_CONTEXT_FORMAT,apiKey,context.toUpperCase()));
        if(keys == null || keys.isEmpty()) {
            return;
        }
        redisCache.remove(keys.toArray(new String[0]));
    }

    //GENERAL
    public void removeCaheByKey(String key) {
        redisCache.remove(key);
    }

    public void RemoveCacheItemFromList(String key, String val) {
        redisCache.removeFromList(key,val);
    }
}

