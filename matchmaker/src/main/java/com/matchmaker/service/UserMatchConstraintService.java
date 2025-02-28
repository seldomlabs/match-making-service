package com.matchmaker.service;

import com.matchmaker.common.dto.UserMatchConstraintsDto;
import com.matchmaker.dao.MatchInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserMatchConstraintService {

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Autowired
    MatchInfoDao matchInfoDao;

    public int getUserAvailableDailyMatchLimit(String userId, UserMatchConstraintsDto userMatchConstraints) throws Exception {
        String key = GeoHashRedisService.getKeyForUserDailyMatchLimit(userId);
        String value = geoHashRedisService.getKey(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        int userMatchCount = matchInfoDao.getUserMatchCountForDay(userId);
        int availableDailyMatchLimit = userMatchConstraints.getMatchLimitPerDay() - userMatchCount;
        geoHashRedisService.setKey(key, String.valueOf(availableDailyMatchLimit));
        return availableDailyMatchLimit;
    }

    public int getUserAvailableMatchLimit(String userId, Date startDate, UserMatchConstraintsDto userMatchConstraints) throws Exception {
        String key = GeoHashRedisService.getKeyForUserMatchLimit(userId);
        String value = geoHashRedisService.getKey(key);
        if (value != null) {
            return Integer.parseInt(value);
        }
        int userMatchCount = matchInfoDao.getUserMatchCountInDate(userId, startDate, new Date());
        int availableMatchLimit = userMatchConstraints.getMatchLimit() - userMatchCount;
        geoHashRedisService.setKey(key, String.valueOf(availableMatchLimit));
        return availableMatchLimit;
    }
}
