package com.matchmaker.common.cache;

import com.matchmaker.common.exception.ApplicationException;
import com.matchmaker.common.redis.RedisService;
import com.matchmaker.util.ApplicationProperties;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;

/**
 * Created by sahilmohindroo on 24/10/18
 */
public abstract class MemoryStoreRedisAbstractCacheService<K, V> extends CacheService<K, V> {

    public static final String REDIS_URL = ApplicationProperties.getInstance().getProperty("redis", "redis.memoryStore.host", "localhost");
    public static final int REDIS_PORT = Integer.parseInt(ApplicationProperties.getInstance().getProperty("redis", "redis.memoryStore.port", "6174"));
    public static final String REDIS_PASSWORD = ApplicationProperties.getInstance().getProperty("redis", "redis.memoryStore.password", null);

    @Autowired
    RedisService redisService;

    protected Jedis getJedis() {
        return redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD);
    }

    public void remove(K key) throws ApplicationException {
        try(Jedis jedis = getJedis()) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            jedis.del(createKeyString(key));
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while fetching from redis");
        }
    }

    protected abstract String createKeyString(K key);
}
