package me.sitech.apifort.cache;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.domain.response.cache.CacheEndpointRes;
import me.sitech.apifort.exceptions.ApiFortInvalidEndpoint;
import me.sitech.apifort.utility.Util;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@ApplicationScoped
public class ApiFortCache {

    //This property used when apply changes on cache structure.
    @ConfigProperty(name = "apifort.cache.version")
    public String cacheVersion;


    private final ListCommands<String, String> redisRangeCommand;
    private final HashCommands<String, String,String> redisHashCommand;
    private final KeyCommands<String> redisCommand;

    private static final String API_FORT_PUBLIC_CERTIFICATES = "apifort-public-certificate";
    private static final String API_FORT_REALM = "apifort-realm";

    //FORMAT apikey-context
    private static final String API_FORT_PROFILE_ENDPOINT_FORMAT = "%s-%s";

    //FORMAT apikey-contextRest-method
    private static final String API_FORT_CONTEXT_METHODS_FORMAT = "%s-%s-%s";
    private static final String API_FORT_CONTEXT_FORMAT = "%s-%s*";

    public ApiFortCache(RedisDataSource dataSource) {
        redisRangeCommand = dataSource.list(String.class);
        redisHashCommand = dataSource.hash(String.class);
        redisCommand = dataSource.key();
    }

    //PROFILE
    public String findCertificateByApiKey(String apiKey){
        return redisHashCommand.hget(API_FORT_PUBLIC_CERTIFICATES,apiKey);
    }

    public String findRealmByApiKey(String apiKey){
        return redisHashCommand.hget(API_FORT_REALM,apiKey);
    }

    public void addProfileCertificate(String apiKey, String certificate,String realm){
        redisHashCommand.hset(API_FORT_PUBLIC_CERTIFICATES,apiKey,certificate);
        redisHashCommand.hset(API_FORT_REALM,apiKey,realm);
    }

    public void deleteProfile(String apiKey){
        redisHashCommand.hdel(API_FORT_PUBLIC_CERTIFICATES,apiKey);
        redisHashCommand.hdel(API_FORT_REALM,apiKey);
    }


    //ENDPOINTS
    public CacheEndpointRes addProfileEndpoint(String apiKey, String context, String method, String urlRegex, String json ) {
        String key = String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),method.toUpperCase());
        if(!redisCommand.exists(key)){
            redisRangeCommand.lpush(key,urlRegex);
            return new CacheEndpointRes(key,addEndpointProperties(apiKey,context,method,urlRegex,json),urlRegex);
        }

        Optional<String> result = redisRangeCommand.lrange(key,0,-1).parallelStream().filter(urlRegex::equals).findFirst();
        if(result.isEmpty()){
            redisRangeCommand.lpush(key,urlRegex);
            return new CacheEndpointRes(key,addEndpointProperties(apiKey,context,method,urlRegex,json),urlRegex);
        }
        return new CacheEndpointRes(key, String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase()),urlRegex);
    }

    public void deleteProfileEndpoint(String apiKey, String context,String method,String regex){
        redisRangeCommand.lrem(String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),
                method.toUpperCase()),0,regex);
        deleteEndpointProperties(apiKey,context,method,regex);
    }

    public String checkEndpointExists(String apiKey, String context,String method,String path){
        String key = String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),method.toUpperCase());
        if(!redisCommand.exists(key)){
            throw new ApiFortInvalidEndpoint(String.format("%s not found",path));
        }
        Optional<String> result = redisRangeCommand.lrange(key,0,-1)
                .parallelStream().filter(regex -> {
                    final Matcher fullMatcher = Pattern.compile(regex).matcher(path);
                    return fullMatcher.find();
                }).findFirst();
        if(result.isEmpty())
            throw new ApiFortInvalidEndpoint(String.format("%s not found",path));
        return findEndpointProperties(apiKey,context,method,result.get());
    }



    //ENDPOINT PROPERTIES
    private String addEndpointProperties(String apiKey, String context,String method,String urlRegex,String json){
        String hashKey = String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase());
        redisHashCommand.hset(hashKey,Util.getSHA1(String.format("%s-%s",method,urlRegex)),json);
        return hashKey;
    }

    private String findEndpointProperties(String apiKey, String context,String method,String urlRegex){
        return redisHashCommand.hget(String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase()),
                Util.getSHA1(String.format("%s-%s",method,urlRegex)));
    }

    private void deleteEndpointProperties(String apiKey, String context,String method,String urlRegex){
        String key = String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase());
        String sha1 = Util.getSHA1(String.format("%s-%s",method,urlRegex));
        if(redisHashCommand.hexists(key,sha1)){
            redisHashCommand.hdel(key,sha1);
        }
    }

    public void deleteByApiKey(String apiKey){
        List<String> keys =  redisCommand.keys(String.format("%s*",apiKey));
        if(keys == null || keys.isEmpty()) {
            return;
        }
        redisCommand.del(keys.toArray(new String[0]));
    }

    public void deleteByApiKeyAndContext(String apiKey,String context){
        List<String> keys =  redisCommand.keys(String.format(API_FORT_CONTEXT_FORMAT,apiKey,context.toUpperCase()));
        if(keys == null || keys.isEmpty()) {
            return;
        }
        redisCommand.del(keys.toArray(new String[0]));
    }

    //GENERAL
    public void del(String key) {
        redisCommand.del(key);
    }

    public void lDel(String key,String val) {
        redisRangeCommand.lrem(key,0,val);
    }
}
