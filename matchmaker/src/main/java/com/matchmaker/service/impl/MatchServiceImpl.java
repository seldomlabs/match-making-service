package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.*;
import com.matchmaker.common.exception.BadRequestException;
import com.matchmaker.constant.MatchStrategies;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.service.MatchStrategy;
import com.matchmaker.service.MatchService;
import com.matchmaker.service.UserService;
import com.matchmaker.util.H3Utility;
import jodd.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class MatchServiceImpl implements MatchService {

    Logger logger = LogManager.getLogger(MatchServiceImpl.class);

    @Autowired
    UserService userService;

    @Resource(name = "getMatchmakingStrategies")
    Map<String, MatchStrategy> matchStrategyMap;

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Override
    public FindMatchResponse findMatchForUser(FindMatchRequest request) throws Exception {
        if (StringUtil.isEmpty(request.getUserId())) {
            throw new BadRequestException("User id is required");
        }
        Double lat = request.getLat();
        Double lon = request.getLon();
        if (lat == null || lon == null) {
            logger.error("Lat or lon is missing");
            throw new BadRequestException("Lat/Lon is required");
        }
        UserDetailsDto userDetails = userService.getUserData(request.getUserId());
        if (userDetails == null) {
            throw new Exception("Unable to fetch user details");
        }
        Integer matchRadius = Optional.ofNullable(request.getMatchRadius()).orElse(userDetails.getMatchRadius());
        List<String> geoHashesInRadius = H3Utility.getH3IndicesInRadius(lat, lon, MatchmakingConstants.H3_RESOLUTION,
                matchRadius);
        List<String> activeUsers = getActiveUsersForGeoHash(geoHashesInRadius);
        BestMatchRequestDto bestMatchRequestDto = new BestMatchRequestDto.BestMatchRequestDtoBuilder().userIdToMatch(request.getUserId()).userLat(lat)
                .userLon(lon).activeUsers(activeUsers).build();
        String matchedUserId = matchStrategyMap.get(MatchStrategies.distance.name()).findBestMatch(bestMatchRequestDto);
        FindMatchResponse response = new FindMatchResponse();
        if (StringUtil.isEmpty(matchedUserId)) {
            response.setStatus(MPResponseStatus.FAILURE.name());
            response.setMessage("Not able to find any match");
        }
        response.setStatus(MPResponseStatus.SUCCESS.name());
        response.setMatchedUserId(matchedUserId);
        return response;
    }

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
}
