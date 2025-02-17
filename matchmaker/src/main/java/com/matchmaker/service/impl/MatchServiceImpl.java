package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.*;
import com.matchmaker.common.exception.BadRequestException;
import com.matchmaker.constant.MatchStrategies;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.service.*;
import com.matchmaker.util.H3Utility;
import jodd.util.StringUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatchServiceImpl implements MatchService {

    Logger logger = LogManager.getLogger(MatchServiceImpl.class);

    @Autowired
    UserService userService;

    @Resource(name = "getMatchmakingStrategies")
    Map<String, MatchStrategy> matchStrategyMap;

    @Autowired
    MatchInfoDao matchInfoDao;

    @Autowired
    MatchHelperService matchHelperService;

    @Override
    public FindMatchResponse findMatchForUser(FindMatchRequest request) throws Exception {
        FindMatchResponse response = new FindMatchResponse();
        response.setStatus(MPResponseStatus.FAILURE.name());

        String userId = request.getUserId();
        if (StringUtil.isEmpty(userId)) {
            throw new BadRequestException("User id is required");
        }
        logger.info("radius is {}", request.getMatchRadius());
        Double lat = request.getLat();
        Double lon = request.getLon();
        UserDetailsDto userDetails = userService.getUserData(userId);
        logger.info("User details from user service : {}", GlobalConstants.objectMapper.writeValueAsString(userDetails));
        if (userDetails == null) {
            throw new Exception("Unable to fetch user details");
        }
        Integer matchRadius = Optional.ofNullable(request.getMatchRadius()).orElse(userDetails.getMatchRadius());
        String userGeoHash = H3Utility.latLonToH3(lat, lon, MatchmakingConstants.H3_RESOLUTION);
        List<String> geoHashesInRadius = H3Utility.getH3IndicesInRadius(userGeoHash, matchRadius);
        logger.info("Geo hashes in radius {}", GlobalConstants.objectMapper.writeValueAsString(geoHashesInRadius));
        //List<String> activeUsers = getActiveUsersForGeoHash(geoHashesInRadius);
        List<String> activeUsersInRadius = matchHelperService.getActiveUsersForGeoHash(geoHashesInRadius);
        logger.info("Active users in redis {}", GlobalConstants.objectMapper.writeValueAsString(activeUsersInRadius));
        BestMatchRequestDto bestMatchRequestDto = new BestMatchRequestDto.BestMatchRequestDtoBuilder().userIdToMatch(request.getUserId()).userLat(lat)
                .userLon(lon).activeUsersInRadius(activeUsersInRadius).userGeoHash(userGeoHash).build();
        BestMatchResponse bestMatchResponse = matchStrategyMap.get(MatchStrategies.distance.name()).findBestMatch(bestMatchRequestDto);
        if (MPResponseStatus.FAILURE.name().equalsIgnoreCase(bestMatchResponse.getStatus())) {
            response.setMessage(bestMatchResponse.getMessage());
            return response;
        }
        response.setMatchId(response.getMatchId());
        response.setStatus(MPResponseStatus.SUCCESS.name());
        response.setMatchedUserId(bestMatchResponse.getMatchedUserId());
        return response;
    }

    private FindMatchResponse checkMatchExistsForRequestId(String requestId, String userId) throws Exception{
        FindMatchResponse response = new FindMatchResponse();
        response.setStatus(MPResponseStatus.FAILURE.name());
        MatchDto matchDto = matchInfoDao.getUsersForMatch(requestId);
        if (matchDto == null) {
            return null;
        }
        List<String> usersForMatch = matchDto.getUsers();
        if (!usersForMatch.contains(userId)) {
            return response;
        }
        usersForMatch = usersForMatch.stream().filter(matchUserId -> !userId.equals(matchUserId))
                .collect(Collectors.toList());
        response.setMatchedUserId(usersForMatch.get(0));
        response.setMatchId(matchDto.getMatchId());
        response.setStatus(MPResponseStatus.SUCCESS.name());
        return response;
    }
}