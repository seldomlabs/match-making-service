package com.matchmaker.service.impl;


import com.matchmaker.common.dto.BestMatchRequestDto;
import com.matchmaker.common.dto.UserDetailsDto;
import com.matchmaker.service.MatchStrategy;
import com.matchmaker.service.UserService;
import com.matchmaker.util.CollectionUtility;
import com.matchmaker.util.GeoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service("distanceMatchStrategy")
public class DistanceMatchStrategy implements MatchStrategy {

    @Autowired
    UserService userService;

    @Override
    public String findBestMatch(BestMatchRequestDto bestMatchRequestDto) {
        Double currentUserLat = bestMatchRequestDto.getUserLat();
        Double currentUserLon = bestMatchRequestDto.getUserLon();
        String userIdToMatch = bestMatchRequestDto.getUserIdToMatch();
        List<String> activeUsers = bestMatchRequestDto.getActiveUsers().stream().filter(userId -> !userId.equalsIgnoreCase(userIdToMatch)).collect(Collectors.toList());
        List<List<String>> shardedUserList = CollectionUtility.shardList(activeUsers, 100);
        List<UserDetailsDto> finalUserDetailsList = new ArrayList<>();
        for (List<String> userList : shardedUserList) {
            Map<String, UserDetailsDto> userDetailsResponse = userService.getUserDataBulk(userList);
            if (!userDetailsResponse.isEmpty()){
                continue;
            }
            List<UserDetailsDto> userDetailsList = new ArrayList<>(userDetailsResponse.values());
            finalUserDetailsList.addAll(userDetailsList);
        }
        double maxDistance = Double.MAX_VALUE;
        String bestMatch = null;
        for (UserDetailsDto matchedUser : finalUserDetailsList) {
            double distance = GeoUtils.getDistance(currentUserLat, currentUserLon,
                    matchedUser.getLat(), matchedUser.getLon());
            if (maxDistance > distance) {
                maxDistance = distance;
                bestMatch = matchedUser.getUserId();
            }
        }
        return bestMatch;
    }
}
