package me.sitech.apifort.cache;

import io.quarkus.redis.datasource.RedisDataSource;
import io.quarkus.redis.datasource.ScanArgs;
import io.quarkus.redis.datasource.hash.HashCommands;
import io.quarkus.redis.datasource.hash.HashScanCursor;
import io.quarkus.redis.datasource.keys.KeyCommands;
import io.quarkus.redis.datasource.list.ListCommands;
import io.quarkus.redis.datasource.value.ValueCommands;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@Slf4j
@ApplicationScoped
public class RedisCache {


    private final ListCommands<String, String> rangeCommand;
    private final HashCommands<String, String, String> hashCommand;
    private final KeyCommands<String> generalCommands;
    private final ValueCommands<String, String> valueCommands;

    public RedisCache(RedisDataSource dataSource) {
        rangeCommand = dataSource.list(String.class);
        hashCommand = dataSource.hash(String.class);
        generalCommands = dataSource.key();
        valueCommands = dataSource.value(String.class);
    }

    public void pushToGroupList(String groupKey, String key, String value) {
        hashCommand.hset(groupKey, key, value);
    }

    public String pullByKey(String groupKey, String key) {
        return hashCommand.hget(groupKey, key);
    }

    public String removeFromGroupList(String groupKey, String key) {
        return hashCommand.hget(groupKey, key);
    }

    public void deleteGroup(String groupKey) {
        generalCommands.del(groupKey);
    }

    public void deleteGroupChildKey(String groupKey, String... keys) {
        hashCommand.hdel(groupKey, keys);
    }

    public void pushToList(String key, String... values) {
        rangeCommand.lpush(key, values);
    }

    public List<String> pullListValues(String key) {
        return rangeCommand.lrange(key, 0, -1);
    }

    public void setExpireKey(String key, String val, int expiration) {
        valueCommands.set(key, val);
        generalCommands.expire(key, expiration);
    }

    public String getValueByKey(String key){
       return valueCommands.get(key);
    }

    public void updateValueByKey(String key , String updatedData){
        valueCommands.set(key,updatedData);
    }

    public boolean isExist(String key){
        return generalCommands.exists(key);
    }

    public boolean isExistInGroupList(String groupName,String key){
        return hashCommand.hexists(groupName,key);
    }
    //GENERAL
    public void remove(String...key) {
        generalCommands.del(key);
    }

    public void removeFromList(String key, String val) {
        rangeCommand.lrem(key, 0, val);
    }

    public HashScanCursor<String, String> getHScan(String groupKey, String pattern){
        return hashCommand.hscan(groupKey, new ScanArgs().match(pattern));
    }

    public List<String> findPatternKeys(String pattern){
        return generalCommands.keys(pattern);
    }
}