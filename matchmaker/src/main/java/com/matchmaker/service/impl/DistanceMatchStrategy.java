package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.*;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.service.MatchHelperService;
import com.matchmaker.service.MatchStrategy;
import com.matchmaker.service.UserService;
import com.matchmaker.service.lock.FindMatchLock;
import com.matchmaker.util.DateConvertUtils;
import com.matchmaker.util.GeoUtils;
import com.matchmaker.util.H3Utility;
import com.matchmaker.util.RandomUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import com.matchmaker.common.constants.MatchmakingConstants.MatchStatus;

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

        String userIdToMatch = bestMatchRequestDto.getUserIdToMatch();
        Double currentUserLat = bestMatchRequestDto.getUserLat();
        Double currentUserLon = bestMatchRequestDto.getUserLon();
        String userGeoHash = bestMatchRequestDto.getUserGeoHash();

        List<String> activeUsersInRadius = bestMatchRequestDto.getActiveUsersInRadius();
        //remove the users (for ex: users which have cancel this user in previous match) from activeUsersInRadius
        activeUsersInRadius = matchHelperService.removeUsersFromActiveUsersInRadius(activeUsersInRadius, userIdToMatch);

        if (CollectionUtils.isEmpty(activeUsersInRadius)) {
            bestMatchResponse.setMessage("No active users in the current set radius");
            return bestMatchResponse;
        }
        Map<String, UserDetailsDto> userDetailsMap = userService.fetchUserLocation(activeUsersInRadius);
        activeUsersInRadius = activeUsersInRadius.stream().filter(userDetailsMap::containsKey).collect(Collectors.toList());
        try{logger.info("activeUsersInRadius{}", GlobalConstants.objectMapper.writeValueAsString(activeUsersInRadius));} catch (Exception ignore) {}
        if (CollectionUtils.isEmpty(userDetailsMap)) {
            bestMatchResponse.setMessage("Unable to fetch user's location");
            return bestMatchResponse;
        }
        List<String> sortedUserList = sortUsersOnDistance(currentUserLat, currentUserLon, activeUsersInRadius, userDetailsMap);
        if (!findMatchLock.takeLock(userIdToMatch)) {
            bestMatchResponse.setMessage("Unable to take lock on user");
            return bestMatchResponse;
        }
        try {
            BestMatchResponse existingMatchResponse = matchHelperService.createMatchResponseIfExists(userIdToMatch);
            if (MPResponseStatus.SUCCESS.name().equalsIgnoreCase(existingMatchResponse.getStatus())) {
                return existingMatchResponse;
            }
            for (String possibleMatchUserId : sortedUserList) {
                UserDetailsDto userDetails;
                if (!findMatchLock.takeLock(possibleMatchUserId)) {
                    bestMatchResponse.setMessage("Unable to take lock on possible match");
                    continue;
                }
                try {
                    String bestMatchForPossibleMatch = matchHelperService.getUserMatchIfExists(possibleMatchUserId);
                    if (bestMatchForPossibleMatch != null) {
                        continue;
                    }
                    userDetails = userDetailsMap.get(possibleMatchUserId);
                    Double bestMatchUserLat = userDetails.getLat();
                    Double bestMatchUserLon = userDetails.getLon();
                    String bestMatchGeoHash = H3Utility.latLonToH3(bestMatchUserLat, bestMatchUserLon, MatchmakingConstants.H3_RESOLUTION);
                    String matchId = RandomUtils.generateUUIDWithTimestamp();
                    matchHelperService.postMatchAction(userGeoHash, bestMatchGeoHash, userIdToMatch, possibleMatchUserId, matchId);
                    bestMatchResponse.setMatchedUserId(possibleMatchUserId);
                    bestMatchResponse.setMatchId(matchId);
                    bestMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
                    return bestMatchResponse;
                } finally {
                    findMatchLock.releaseLock(possibleMatchUserId);
                }
            }
        } finally {
            findMatchLock.releaseLock(userIdToMatch);
        }
        try{logger.info("Final user details list {}", GlobalConstants.objectMapper.writeValueAsString(userDetailsMap));} catch (Exception ignore) {}
        bestMatchResponse.setMessage("Unable to find match");
        return bestMatchResponse;
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
