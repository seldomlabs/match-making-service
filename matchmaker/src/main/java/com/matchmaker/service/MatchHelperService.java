package com.matchmaker.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchHelperService {

    @Autowired
    GeoHashRedisService geoHashRedisService;

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
}
