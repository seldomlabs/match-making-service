package com.matchmaker.service;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.BestMatchResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.util.DateConvertUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchHelperService {

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Autowired
    MatchInfoDao matchInfoDao;

    Logger logger = LogManager.getLogger(MatchHelperService.class);

    public List<String> getActiveUsersForGeoHash(List<String> geoHashList) {
        List<String> finalUserList = new ArrayList<>();
        try {
            String[] keys = new String[geoHashList.size()];
            int index = 0;
            for (String parameter : geoHashList) {
                keys[index++] = GeoHashRedisService.getKeyForActiveUsersGeoHashSet(parameter);
            }
            Set<String>[] vals = geoHashRedisService.getMembersOfSet(keys);
            for (Set<String> val : vals) {
                if (val != null) {
                    finalUserList.addAll(val);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in getUsersForGeoHash", e);
        }
        return finalUserList;
    }

    public Map<String, Set<String>> getActiveUsersMapForGeoHash(List<String> geoHashList) {
        Map<String, Set<String>> activeUsersMap = new HashMap<>();
        try {
            String[] keys = new String[geoHashList.size()];
            int index = 0;
            for (String parameter : geoHashList) {
                keys[index++] = GeoHashRedisService.getKeyForActiveUsersGeoHashSet(parameter);
            }
            Set<String>[] vals = geoHashRedisService.getMembersOfSet(keys);
            int ind = 0;
            for (String hash : geoHashList) {
                if (vals[ind] != null) {
                    activeUsersMap.put(hash, vals[ind]);
                }
                ind++;
            }
        } catch (Exception e) {
            logger.error("Exception in getUsersForGeoHash", e);
        }
        return activeUsersMap;
    }

    public void postMatchAction(String userGeoHash, String matchedUserGeoHash, String userId, String matchedUserId, String matchId) {
        try {
            logger.info("userGeoHash" + userGeoHash);
            logger.info("userGeoHash" + matchedUserGeoHash);
            geoHashRedisService.removeMemberFromSet(GeoHashRedisService.getKeyForActiveUsersGeoHashSet(userGeoHash), userId);
            geoHashRedisService.removeMemberFromSet(GeoHashRedisService.getKeyForActiveUsersGeoHashSet(matchedUserGeoHash), matchedUserId);
            geoHashRedisService.setKey(GeoHashRedisService.getKeyForUserMatch(userId), matchedUserId);
            geoHashRedisService.setKey(GeoHashRedisService.getKeyForUserMatch(matchedUserId), userId);
            geoHashRedisService.setKey(GeoHashRedisService.getKeyForUserMatchId(matchedUserId), matchId);
            geoHashRedisService.setKey(GeoHashRedisService.getKeyForUserMatchId(userId), matchId);
        } catch (Exception e) {
            logger.error("Exception in postMatchAction", e);
        }
    }

    public List<String> removeUsersFromActiveUsersInRadius(List<String> activeUsersInRadius, String userId) {
        Date cancelDateThreshold = DateConvertUtils.getDateByOffset(MatchmakingConstants.CANCEL_MATCH_CHECK_DAYS_THRESHOLD * -1);
        try {
            Set<String> userAlreadyCanceled = getMatchedUsersForStatusInStartDate(userId, MatchmakingConstants.MatchStatus.CANCELED.name(), cancelDateThreshold);
            activeUsersInRadius = activeUsersInRadius.stream().filter(userInRadius -> !userAlreadyCanceled.contains(userInRadius)).collect(Collectors.toList());
            return activeUsersInRadius;
        } catch (Exception e) {
            logger.error("Exception in removeUsersFromActiveUsersInRadius", e);
        }
        return new ArrayList<>();
    }

    private Set<String> getMatchedUsersForStatusInStartDate(String userId, String status, Date startDate) throws Exception {
        List<String> matchIds = matchInfoDao.getUserMatchIdsForStatusInDate(userId, status, startDate, null);
        logger.info("cancelled match ids" + GlobalConstants.objectMapper.writeValueAsString(matchIds));
        if (matchIds.isEmpty()) {
            return new HashSet<>();
        }
        return matchInfoDao.getUserForMatchIds(matchIds);
    }

    public BestMatchResponse createMatchResponseIfExists(String userId) {
        BestMatchResponse bestMatchResponse = new BestMatchResponse();
        bestMatchResponse.setStatus(MPResponseStatus.FAILURE.name());

        String bestMatchForCurrentUser = getUserMatchIfExists(userId);
        if (bestMatchForCurrentUser == null) {
            bestMatchResponse.setMessage("No match exists for the user");
            return bestMatchResponse;
        }
        String matchId = getUserMatchIdIfExists(userId);
        bestMatchResponse.setMatchId(matchId);
        bestMatchResponse.setMatchedUserId(bestMatchForCurrentUser);
        bestMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return bestMatchResponse;
    }

    private String getUserMatchIdIfExists(String userId) {
        try {
            return geoHashRedisService.getKey(GeoHashRedisService.getKeyForUserMatchId(userId));
        } catch (Exception e) {
            logger.error("Exception in getUserMatchIfExists", e);
        }
        return null;
    }

    public String getUserMatchIfExists(String userId) {
        try {
            return geoHashRedisService.getKey(GeoHashRedisService.getKeyForUserMatch(userId));
        } catch (Exception e) {
            logger.error("Exception in getUserMatchIfExists", e);
        }
        return null;
    }

    public void removeUsersMatchIdKeyFromRedis(Set<String> userIds) throws Exception {
        for (String userId : userIds) {
            geoHashRedisService.deleteKey(GeoHashRedisService.getKeyForUserMatchId(userId));
        }
    }

    public void removeUsersMatchKeysFromRedis(Set<String> userIds) throws Exception {
        for (String userId : userIds) {
            geoHashRedisService.deleteKey(GeoHashRedisService.getKeyForUserMatch(userId));
        }
    }

    public void removeUsersMatchRelatedKeysFromRedis(String matchId) throws Exception {
        Set<String> userIds = matchInfoDao.getUserForMatchIds(Collections.singletonList(matchId));
        removeUsersMatchKeysFromRedis(userIds);
        removeUsersMatchIdKeyFromRedis(userIds);
    }
}
