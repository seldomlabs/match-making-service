package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.*;
import com.matchmaker.common.exception.BadRequestException;
import com.matchmaker.constant.MatchStrategies;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.*;
import com.matchmaker.util.DateConvertUtils;
import com.matchmaker.util.H3Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.solr.common.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.*;

@Service
public class MatchServiceImpl implements MatchService {

    Logger logger = LogManager.getLogger(MatchServiceImpl.class);

    @Autowired
    UserService userService;

    @Resource(name = "getMatchmakingStrategies")
    Map<String, MatchStrategy> matchStrategyMap;

    @Autowired
    MatchHelperService matchHelperService;

    @Autowired
    UserMatchConstraintService userMatchConstraintService;

    @Autowired
    SubscriptionService subscriptionService;

    @Override
    public FindMatchResponse findMatchForUser(FindMatchRequest request) throws Exception {
        FindMatchResponse response = new FindMatchResponse();
        response.setStatus(MPResponseStatus.FAILURE.name());

        String userId = request.getUserId();
        if (StringUtils.isEmpty(userId)) {
            throw new BadRequestException("User id is required");
        }
        BestMatchResponse existingMatchResponse = matchHelperService.createMatchResponseIfExists(userId);
        if (MPResponseStatus.SUCCESS.name().equalsIgnoreCase(existingMatchResponse.getStatus())) {
            response.setMatchId(existingMatchResponse.getMatchId());
            response.setMatchedUserId(existingMatchResponse.getMatchedUserId());
            response.setStatus(MPResponseStatus.SUCCESS.name());
            return response;
        }
        MPResponse matchLimitsValidationResponse = validateMatchLimits(userId);
        if (MPResponseStatus.FAILURE.name().equalsIgnoreCase(matchLimitsValidationResponse.getStatus())) {
            response.setMessage(matchLimitsValidationResponse.getMessage());
            return response;
        }

        Double lat = request.getLat();
        Double lon = request.getLon();
        UserDetailsDto userDetails = userService.getUserData(userId);
        logger.info("User details from user service : {}", GlobalConstants.objectMapper.writeValueAsString(userDetails));
        if (userDetails == null) {
            throw new Exception("Unable to fetch user details");
        }
        Integer matchRadius = userDetails.getMatchRadius();
        if (matchRadius == null) {
            throw new BadRequestException("Unable to get user match radius");
        }
        String userGeoHash = H3Utility.latLonToH3(lat, lon, MatchmakingConstants.H3_RESOLUTION);
        List<String> geoHashesInRadius = H3Utility.getH3IndicesInRadius(userGeoHash, matchRadius);
        logger.info("Geo hashes in radius {}", GlobalConstants.objectMapper.writeValueAsString(geoHashesInRadius));
        List<String> activeUsersInRadius = matchHelperService.getActiveUsersForGeoHash(geoHashesInRadius);
        logger.info("Active users in redis {}", GlobalConstants.objectMapper.writeValueAsString(activeUsersInRadius));
        BestMatchRequestDto bestMatchRequestDto = new BestMatchRequestDto.BestMatchRequestDtoBuilder().userIdToMatch(request.getUserId()).userLat(lat)
                .userLon(lon).activeUsersInRadius(activeUsersInRadius).userGeoHash(userGeoHash).build();
        BestMatchResponse bestMatchResponse = matchStrategyMap.get(MatchStrategies.distance.name()).findBestMatch(bestMatchRequestDto);
        if (MPResponseStatus.FAILURE.name().equalsIgnoreCase(bestMatchResponse.getStatus())) {
            response.setMessage(bestMatchResponse.getMessage());
            return response;
        }
        response.setMatchId(bestMatchResponse.getMatchId());
        response.setStatus(MPResponseStatus.SUCCESS.name());
        response.setMatchedUserId(bestMatchResponse.getMatchedUserId());
        return response;
    }

    private MPResponse validateMatchLimits(String userId) throws Exception {
        MPResponse mpResponse = new MPResponse();
        mpResponse.setStatus(MPResponseStatus.FAILURE.name());

        UserSubscriptionDetailsResponse subscriptionDetailsResponse = subscriptionService.getUserSubscriptionDetails(userId);
        if (!MPResponseStatus.SUCCESS.name().equalsIgnoreCase(subscriptionDetailsResponse.getStatus())){
            mpResponse.setMessage("Match limits validation failed");
            return mpResponse;
        }
        Date subscriptionStartDate = subscriptionDetailsResponse.getSubscriptionStartDate();
        Date subscriptionEndDate = subscriptionDetailsResponse.getSubscriptionEndDate();
        Integer totalMatchLimits = subscriptionDetailsResponse.getTotalMatchLimits();
        Integer dailyMatchLimit = subscriptionDetailsResponse.getDailyMatchLimits();
        Integer matchCount = userMatchConstraintService.getUserMatchCount(userId, subscriptionStartDate, subscriptionEndDate);
        if (totalMatchLimits - matchCount <= 0) {
            mpResponse.setMessage("User total limits are exhausted");
            return mpResponse;
        }
        Integer dailyMatchCount = userMatchConstraintService.getUserDailyMatchCount(userId);
        if (dailyMatchLimit - dailyMatchCount <= 0) {
            mpResponse.setMessage("User daily limits are exhausted");
            return mpResponse;
        }
        mpResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return mpResponse;
    }
}