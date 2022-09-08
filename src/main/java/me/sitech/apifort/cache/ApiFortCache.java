package me.sitech.apifort.cache;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import lombok.extern.slf4j.Slf4j;
import me.sitech.apifort.exceptions.ApiFortInvalidEndpoint;
import me.sitech.apifort.utility.Util;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
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

    //FORMAT {api-key}-{context}
    private static final String API_FORT_PROFILE_ENDPOINT_FORMAT = "%s-%s";

    //FORMAT {api-key}-{context}-{rest-method}
    private static final String API_FORT_CONTEXT_METHODS_FORMAT = "%s-%s-%s";

    private RedisDataSource ds;

    public ApiFortCache(RedisDataSource ds) {
        redisRangeCommand = ds.list(String.class);
        redisHashCommand = ds.hash(String.class);
        redisCommand = ds.key();
    }

    //PROFILE
    public String findCertificateByApiKey(String apiKey){
        return redisHashCommand.hget(API_FORT_PUBLIC_CERTIFICATES,apiKey);
    }

    public void addProfileCertificate(String apiKey, String certificate){
        redisHashCommand.hset(API_FORT_PUBLIC_CERTIFICATES,apiKey,certificate);
    }

    public void deleteProfileCertificate(String apiKey){
        redisHashCommand.hdel(API_FORT_PUBLIC_CERTIFICATES,apiKey);
    }


    //ENDPOINTS
    public void addProfileEndpoint(String apiKey, String context,String method,String regex,String json ) {
        String key = String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),method.toUpperCase());
        if(!redisCommand.exists(key)){
            redisRangeCommand.lpush(key,regex);
            addEndpointProperties(apiKey,context,regex,json);
            return;
        }
        Optional<String> result = redisRangeCommand.lrange(key,0,-1).parallelStream().filter(regex::equals).findFirst();
        if(result.isEmpty()){
            redisRangeCommand.lpush(key,regex);
            addEndpointProperties(apiKey,context,regex,json);
        }
    }


    public void deleteProfileEndpoint(String apiKey, String context,String method,String regex){
        String key = String.format(API_FORT_CONTEXT_METHODS_FORMAT,apiKey,context.toUpperCase(),method.toUpperCase());
        redisRangeCommand.lrem(key,0,regex);
        deleteEndpointProperties(apiKey,context,method);
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
        return findEndpointProperties(apiKey,context,result.get());
    }


    //ENDPOINT PROPERTIES
    private void addEndpointProperties(String apiKey, String context,String regex,String json){
        redisHashCommand.hset(String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase()),Util.getSHA1(regex),json);
    }

    private String findEndpointProperties(String apiKey, String context,String regex){
        return redisHashCommand.hget(String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase()),Util.getSHA1(regex));
    }

    private void deleteEndpointProperties(String apiKey, String context,String regex){
        String key = String.format(API_FORT_PROFILE_ENDPOINT_FORMAT,apiKey,context.toUpperCase());
        String sha1 = Util.getSHA1(regex);
        if(redisHashCommand.hexists(key,sha1)){
            redisHashCommand.hdel(key,sha1);
        }
    }

    //GENERAL
    public void del(String key) {
        redisCommand.del(key);
    }

    public void lDel(String key,String val) {
        redisRangeCommand.lrem(key,0,val);
    }
}
