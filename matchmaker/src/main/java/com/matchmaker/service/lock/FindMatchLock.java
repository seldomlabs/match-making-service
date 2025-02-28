package com.matchmaker.service.lock;

import com.matchmaker.common.redis.RedisLock;
import com.matchmaker.util.ApplicationProperties;
import org.springframework.stereotype.Service;

@Service("findMatchLock")
public class FindMatchLock extends RedisLock {

    private static final String GENERIC_LOCK_TEMPLATE = ApplicationProperties.getInstance().getProperty(
            "lock", "find.match.lock", "FIND_MATCH_LOCK_%s");

    @Override
    public String createKey(String id) {
        return String.format(GENERIC_LOCK_TEMPLATE, id);
    }

    public boolean takeLock(String key) {
        return this.lock(key, 60, 3);
    }

    public void releaseLock(String key) {
        this.unlock(key);
    }
}
