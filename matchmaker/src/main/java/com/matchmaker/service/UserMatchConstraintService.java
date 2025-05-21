package com.matchmaker.service;

import com.matchmaker.common.constants.ThreadConstants;
import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.service.impl.MatchServiceImpl;
import com.matchmaker.util.DateConvertUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
public class UserMatchConstraintService {

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Autowired
    MatchInfoDao matchInfoDao;

    Logger logger = LogManager.getLogger(UserMatchConstraintService.class);

    public int getUserMatchCount(String userId, Date startDate, Date endDate) throws Exception {
        String key = GeoHashRedisService.getKeyForUserMatchCount(userId);
        try {
            String availableMatchesString = geoHashRedisService.getKey(key);
            if (availableMatchesString != null) {
                return Integer.parseInt(availableMatchesString);
            }
        } catch (Exception e) {
            logger.error("Exception while getting user match count from redis ", e);
        }
        int userMatchCount = matchInfoDao.getUserMatchCountInDate(userId, startDate, endDate);
        Integer expiry = endDate != null ? (int) (endDate.getTime() - startDate.getTime()) / 1000 : null;
        setMatchCountKeyInRedisAsync(key, expiry, userMatchCount);
        return userMatchCount;
    }

    public int getUserDailyMatchCount(String userId) throws Exception {
        String key = GeoHashRedisService.getKeyForUserDailyMatchCount(userId);
        try {
            String availableDailyMatchesString = geoHashRedisService.getKey(key);
            if (availableDailyMatchesString != null) {
                return Integer.parseInt(availableDailyMatchesString);
            }
        } catch (Exception e) {
            logger.error("Exception while getting user daily match count from redis ", e);
        }
        int userDailyMatchCount = matchInfoDao.getUserMatchCountForDay(userId);
        int expiry = (int) (DateConvertUtils.getEndOfDate(new Date()).getTime() - new Date().getTime()) / 1000;
        setMatchCountKeyInRedisAsync(key, expiry, userDailyMatchCount);
        return userDailyMatchCount;
    }

    private void setMatchCountKeyInRedisAsync(String key, Integer expiry, int value) {
        ThreadConstants.SET_USER_MATCH_COUNT_BATCH_EXECUTOR.submit(() -> {
            try {
                if (expiry != null) {
                    logger.info("expiry is " + expiry);
                    geoHashRedisService.setKey(key, String.valueOf(value), expiry);
                } else {
                    geoHashRedisService.setKey(key, String.valueOf(value));
                }
            } catch (Exception e) {
                logger.error("Exception while setting user match count in redis ", e);
            }
        });
    }
}
