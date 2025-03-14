package com.matchmaker.service;

import com.matchmaker.common.exception.ApplicationException;
import com.matchmaker.common.redis.RedisService;
import com.matchmaker.util.ApplicationProperties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service("geoHashRedisService")
public class GeoHashRedisService {

    Logger logger = LogManager.getLogger(GeoHashRedisService.class);

    public static final String REDIS_URL = ApplicationProperties.getInstance().getProperty("redis", "redis.geoHash.host", "localhost");
    public static final int REDIS_PORT = Integer.parseInt(ApplicationProperties.getInstance().getProperty("redis", "redis.geoHash.port", "6379"));
    public static final String REDIS_PASSWORD = ApplicationProperties.getInstance().getProperty("redis", "redis.geoHash.password", null);

    @Autowired
    RedisService redisService;

    public static String getKeyForActiveUsersGeoHashSet(String geoHash) {
        return "GEO_HASH_" + geoHash;
    }

    public static String getKeyForUserGeoHashMapping(String userId) {
        return "USER_GEO_HASH_" + userId;
    }

    public static String getKeyForUserLocation(String userId) {
        return "USER_GEO_LOCATION_" + userId;
    }

    public static String getKeyForUserMatch(String userId) {
        return "USER_MATCH_" + userId;
    }

    public static String getKeyForUserMatchLimit(String userId) {
        return "USER_MATCH_LIMIT_" + userId;
    }

    public static String getKeyForUserDailyMatchLimit(String userId) {
        return "USER_MATCH_LIMIT_DAILY_" + userId;
    }

    public static String getKeyForUserMatchId(String userId) {
        return "USER_MATCH_ID_" + userId;
    }

    public List<String> getDataForKeys(String[] keys) throws ApplicationException {
        if (keys.length == 0)
            return null;

        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            return jedis.mget(keys);
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while putting in redis");
        }
    }

    public Set<String>[] getMembersOfSet(String[] keys) throws ApplicationException {
        Set<String>[] finalSet = new Set[keys.length];
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            Pipeline pipeline = jedis.pipelined();
            Response<Set<String>>[] response = new Response[keys.length];
            for (int i = 0; i < keys.length; i++) {
                response[i] = pipeline.smembers(keys[i]);
            }
            pipeline.sync();
            for (int i = 0; i < keys.length; i++) {
                finalSet[i] = response[i].get();
            }
        } catch (Exception e) {
            logger.error("Exception in getMembersOfSet", e);
            throw new ApplicationException("error while putting in redis");
        }
        return finalSet;
    }

    public void addMemberToSet(String key, String value) throws ApplicationException {
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("Exception in getMembersOfSet", e);
            throw new ApplicationException("error while putting in redis");
        }
    }

    public void setKey(String key, String value) throws ApplicationException {
        if (value == null) return;
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            jedis.set(key, value);
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while putting in redis");
        }
    }

    public void setKey(String key, String value, int expiry) throws ApplicationException {
        if (value == null) return;
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            jedis.setex(key, expiry, value);
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while putting in redis");
        }
    }

    public String getKey(String key) throws ApplicationException {
        String data = null;
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            data = jedis.get(key);
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while putting in redis");
        }
        return data;
    }

    public void removeMemberFromSet(String key, String value) throws ApplicationException {
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("Exception in getMembersOfSet", e);
            throw new ApplicationException("error while putting in redis");
        }
    }

    public void deleteKey(String key) throws ApplicationException {
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            jedis.del(key);
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while putting in redis");
        }
    }

    public void updateCount(String key, int cnt) throws ApplicationException {
        try (Jedis jedis = redisService.getResourceForUrlWithPort(REDIS_URL, REDIS_PORT, REDIS_PASSWORD)) {
            if (jedis == null) {
                logger.error("redis not available");
                throw new ApplicationException("redis not available");
            }
            if (!jedis.exists(key)) {
                return;
            }
            if (cnt > 0) {
                jedis.incr(key);
            }
            if (cnt < 0) {
                jedis.decr(key);
            }
        } catch (Exception e) {
            logger.error("Error fetching redis", e);
            throw new ApplicationException("error while putting in redis");
        }
    }
}
