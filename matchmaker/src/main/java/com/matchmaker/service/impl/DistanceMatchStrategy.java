package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.*;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.service.MatchHelperService;
import com.matchmaker.service.MatchStrategy;
import com.matchmaker.service.UserService;
import com.matchmaker.service.lock.FindMatchLock;
import com.matchmaker.util.GeoUtils;
import com.matchmaker.util.H3Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

@Service("distanceMatchStrategy")
public class DistanceMatchStrategy implements MatchStrategy {

    Logger logger = LogManager.getLogger(DistanceMatchStrategy.class);

    @Autowired
    UserService userService;

    @Autowired
    FindMatchLock findMatchLock;

    @Autowired
    MatchHelperService matchHelperService;

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Override
    public BestMatchResponse findBestMatch(BestMatchRequestDto bestMatchRequestDto) {
        BestMatchResponse bestMatchResponse = new BestMatchResponse();
        bestMatchResponse.setStatus(MPResponseStatus.FAILURE.name());

        Double currentUserLat = bestMatchRequestDto.getUserLat();
        Double currentUserLon = bestMatchRequestDto.getUserLon();
        String userIdToMatch = bestMatchRequestDto.getUserIdToMatch();
        String userGeoHash = bestMatchRequestDto.getUserGeoHash();
        List<String> activeUsersInRadius = bestMatchRequestDto.getActiveUsersInRadius();
        activeUsersInRadius = activeUsersInRadius.stream().filter(userId -> !userId.equalsIgnoreCase(userIdToMatch)).collect(Collectors.toList());
        Map<String, UserDetailsDto> userDetailsMap = userService.fetchUserLocation(activeUsersInRadius);
        activeUsersInRadius = activeUsersInRadius.stream().filter(userDetailsMap::containsKey).collect(Collectors.toList());
        try{logger.info("activeUsersInRadius{}", GlobalConstants.objectMapper.writeValueAsString(activeUsersInRadius));} catch (Exception ignore) {}
        if (CollectionUtils.isEmpty(userDetailsMap)) {
            bestMatchResponse.setMessage("Unable to fetch user's location");
            return bestMatchResponse;
        }
        List<String> sortedUserList = sortUsersOnDistance(currentUserLat, currentUserLon, activeUsersInRadius, userDetailsMap);
        String bestMatch = null;
        for (String possibleMatchUserId : sortedUserList) {
            UserDetailsDto userDetails;
            if (!findMatchLock.takeLock(userIdToMatch)) {
                bestMatchResponse.setMessage("Unable to take lock on user");
                return bestMatchResponse;
            }
            if (!findMatchLock.takeLock(possibleMatchUserId)) {
                bestMatchResponse.setMessage("Unable to take lock on possible match");
                return bestMatchResponse;
            }
            try {
                String bestMatchForCurrentUser = getUserMatchIfExists(userIdToMatch);
                if (bestMatchForCurrentUser != null) {
                    bestMatch = bestMatchForCurrentUser;
                    break;
                }
                String bestMatchForPossibleMatch = getUserMatchIfExists(possibleMatchUserId);
                if (bestMatchForPossibleMatch != null) {
                    continue;
                }
                bestMatch = possibleMatchUserId;
                userDetails = userDetailsMap.get(bestMatch);
                Double bestMatchUserLat = userDetails.getLat();
                Double bestMatchUserLon = userDetails.getLon();
                String bestMatchGeoHash = H3Utility.latLonToH3(bestMatchUserLat, bestMatchUserLon, MatchmakingConstants.H3_RESOLUTION);
                matchHelperService.postMatchAction(userGeoHash, bestMatchGeoHash, userIdToMatch, bestMatch);
                break;
            } finally {
                findMatchLock.releaseLock(userIdToMatch);
                findMatchLock.releaseLock(possibleMatchUserId);
            }
        }
        if (bestMatch == null) {
            bestMatchResponse.setMessage("Unable to find match");
            return bestMatchResponse;
        }
        try{logger.info("Final user details list {}", GlobalConstants.objectMapper.writeValueAsString(userDetailsMap));} catch (Exception ignore) {}
        bestMatchResponse.setMatchedUserId(bestMatch);
        bestMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return bestMatchResponse;
    }

    private String getUserMatchIfExists(String userId) {
        try {
            return geoHashRedisService.getKey(GeoHashRedisService.getKeyForUserMatch(userId));
        } catch (Exception e) {
            logger.error("Exception in getUserMatchIfExists", e);
        }
        return null;
    }

    private List<String> sortUsersOnDistance(Double currentUserLat, Double currentUserLon, List<String> userList,
                                             Map<String, UserDetailsDto> userDetailsMap) {
        Map<String, Double> hashDistMap = new HashMap<>();
        for (String userId : userList) {
            UserDetailsDto userDetails = userDetailsMap.get(userId);
            if (userDetails != null) {
                double distance = GeoUtils.getDistance(currentUserLat, currentUserLon,
                        userDetails.getLat(), userDetails.getLon());
                hashDistMap.put(userId, distance);
            }
        }
        userList.sort(Comparator.comparingDouble(hashDistMap::get));
        return userList;
    }

    private List<String> sortHashesWithNearestMatch(Double currentUserLat, Double currentUserLon,
                                                    Map<String, UserDetailsDto> userDetailsMap,
                                                    Map<String, Set<String>> activeUsersMap) {
        Map<String, Double> hashDistMap = new HashMap<>();
        for (Map.Entry<String, Set<String>> mp : activeUsersMap.entrySet()) {
            Set<String> activeUserSet = mp.getValue();
            if (!CollectionUtils.isEmpty(activeUserSet)) {
                double minDist = minimumDistanceOfUserInHash(currentUserLat, currentUserLon, userDetailsMap, activeUserSet);
                hashDistMap.put(mp.getKey(), minDist);
            }
        }
        List<String> geoHash = new ArrayList<>(hashDistMap.keySet());
        geoHash.sort(Comparator.comparingDouble(hashDistMap::get));
        return geoHash;
    }

    private double minimumDistanceOfUserInHash(Double currentUserLat, Double currentUserLon,
                                               Map<String, UserDetailsDto> userDetailsMap,
                                               Set<String> userList) {
        double minDist = Double.MAX_VALUE;
        for (String userId : userList) {
            if (userDetailsMap.containsKey(userId)) {
                UserDetailsDto userDetailsDto = userDetailsMap.get(userId);
                double distance = GeoUtils.getDistance(currentUserLat, currentUserLon,
                        userDetailsDto.getLat(), userDetailsDto.getLon());
                minDist = Math.min(minDist, distance);
            }
        }
        return minDist;
    }
}
