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
        String key = GeoHashRedisService.getKeyForUserMatchLimit(userId);
        String availableMatchesString = geoHashRedisService.getKey(key);
        if (availableMatchesString != null) {
            return Integer.parseInt(availableMatchesString);
        }
        long expiry = (endDate.getTime() - startDate.getTime()) / 1000;
        int availableMatches = matchInfoDao.getUserMatchCountInDate(userId, startDate, endDate);
        geoHashRedisService.setKey(key, String.valueOf(availableMatches), (int) expiry);
        return availableMatches;
    }

    public int getUserDailyMatchCount(String userId) throws Exception {
        String key = GeoHashRedisService.getKeyForUserDailyMatchLimit(userId);
        String availableDailyMatchesString = geoHashRedisService.getKey(key);
        if (availableDailyMatchesString != null) {
            return Integer.parseInt(availableDailyMatchesString);
        }
        int availableMatches = matchInfoDao.getUserMatchCountForDay(userId);
        long expiry = (DateConvertUtils.getEndOfDate(new Date()).getTime() - new Date().getTime()) / 1000;
        geoHashRedisService.setKey(key, String.valueOf(availableMatches), (int) expiry);
        return availableMatches;
    }
}
