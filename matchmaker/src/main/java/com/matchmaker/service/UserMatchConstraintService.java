package com.matchmaker.service;

import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.util.DateConvertUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserMatchConstraintService {

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Autowired
    MatchInfoDao matchInfoDao;

    public int getUserMatchCount(String userId, Date startDate, Date endDate) throws Exception {
        String key = GeoHashRedisService.getKeyForUserMatchCount(userId);
        String availableMatchesString = geoHashRedisService.getKey(key);
        if (availableMatchesString != null) {
            return Integer.parseInt(availableMatchesString);
        }
        long expiry = (endDate.getTime() - startDate.getTime()) / 1000;
        int userMatchCount = matchInfoDao.getUserMatchCountInDate(userId, startDate, endDate);
        geoHashRedisService.setKey(key, String.valueOf(userMatchCount), (int) expiry);
        return userMatchCount;
    }

    public int getUserDailyMatchCount(String userId) throws Exception {
        String key = GeoHashRedisService.getKeyForUserDailyMatchCount(userId);
        String availableDailyMatchesString = geoHashRedisService.getKey(key);
        if (availableDailyMatchesString != null) {
            return Integer.parseInt(availableDailyMatchesString);
        }
        int userDailyMatchCount = matchInfoDao.getUserMatchCountForDay(userId);
        long expiry = (DateConvertUtils.getEndOfDate(new Date()).getTime() - new Date().getTime()) / 1000;
        geoHashRedisService.setKey(key, String.valueOf(userDailyMatchCount), (int) expiry);
        return userDailyMatchCount;
    }
}
