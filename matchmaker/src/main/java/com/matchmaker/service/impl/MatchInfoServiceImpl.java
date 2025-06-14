package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants.MatchStatus;
import com.matchmaker.common.db.service.CommonDbService;
import com.matchmaker.common.dto.*;
import com.matchmaker.common.exception.ApplicationException;
import com.matchmaker.common.exception.NotFoundException;
import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.model.MatchInfo;
import com.matchmaker.model.UserMatchMapping;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.service.MatchHelperService;
import com.matchmaker.service.MatchInfoService;
import com.matchmaker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service("matchInfoServiceImpl")
public class MatchInfoServiceImpl implements MatchInfoService {

    @Autowired
    MatchInfoDao matchInfoDao;

    @Autowired
    CommonDbService commonDbService;

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Autowired
    MatchHelperService matchHelperService;

    @Autowired
    UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchInfoAddResponse setMatchInfo(MatchInfoAddRequest request) throws Exception {
        MatchInfoAddResponse createMatchResponse = new MatchInfoAddResponse();
        createMatchResponse.setStatus(MPResponseStatus.FAILURE.name());

        String matchId = request.getMatchId();
        MatchInfo matchInfo = matchInfoDao.getMatchInfoFromMatchId(matchId);
        if (matchInfo != null) {
            createMatchResponse.setMatchId(matchId);
            createMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
            return createMatchResponse;
        }
        matchInfo = setMatchInfo(request.getMeetingLat(), request.getMeetingLon(), matchId, request.getMatchStatus());
        setUserMatchMapping(request.getUsers(), matchInfo.getId());
        updateUserMatchCounts(request.getUsers(), 1);
        //matchHelperService.removeUsersMatchIdKeyFromRedis(request.getUsers());

        createMatchResponse.setMatchId(matchId);
        createMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return createMatchResponse;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchInfoAddResponse updateMatchInfo(UpdateMatchInfoRequest request) throws Exception {
        MatchInfoAddResponse updateMatchResponse = new MatchInfoAddResponse();
        updateMatchResponse.setStatus(MPResponseStatus.FAILURE.name());

        String matchId = request.getMatchId();
        MatchInfo matchInfo = matchInfoDao.getMatchInfoFromMatchId(matchId);
        if (matchInfo == null) {
            updateMatchResponse.setMatchId(matchId);
            updateMatchResponse.setMessage("Match does not exist for given match id");
            return updateMatchResponse;
        }
        if (MatchStatus.CANCELED.name().equalsIgnoreCase(matchInfo.getMatchStatus())) {
            updateMatchResponse.setStatus(MPResponseStatus.FAILURE.name());
            updateMatchResponse.setMessage("Can't change status");
        }
        if (request.getMatchStatus() != null) {
            matchInfo.setMatchStatus(request.getMatchStatus());
        }
        if (request.getMeetingLat() != null) {
            matchInfo.setMeetingLat(request.getMeetingLat());
        }
        if (request.getMeetingLon() != null) {
            matchInfo.setMeetingLon(request.getMeetingLon());
        }
        if (request.getMeetingTime() != null) {
            matchInfo.setMeetingTime(request.getMeetingTime());
        }
        if (MatchStatus.CANCELED.name().equalsIgnoreCase(request.getMatchStatus())) {
            matchHelperService.removeUsersMatchRelatedKeysFromRedis(matchId);
        }
        matchInfo = commonDbService.updateEntity(matchInfo);
        updateMatchResponse.setMatchId(matchInfo.getMatchId());
        updateMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return updateMatchResponse;
    }

    private MatchInfo setMatchInfo(Double meetingLat, Double meetingLon, String matchId, String matchStatus) throws Exception {
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.setMeetingLat(meetingLat);
        matchInfo.setMeetingLon(meetingLon);
        matchInfo.setMatchId(matchId);
        matchInfo.setMatchStatus(matchStatus);
        matchInfo = commonDbService.updateEntity(matchInfo);
        return matchInfo;
    }

    private void setUserMatchMapping(Set<String> userList, Long matchInfoId) throws Exception {
        Map<String, UserDetailsDto> userDetailsMap = userService.fetchUserLocation(new ArrayList<>(userList));
        for (String userId : userList) {
            UserMatchMapping userMatchMapping = new UserMatchMapping();
            userMatchMapping.setMatchInfoId(matchInfoId);
            userMatchMapping.setUserId(userId);
            UserDetailsDto userDetailsDto = userDetailsMap.get(userId);
            if (userDetailsDto != null) {
                userMatchMapping.setUserLat(userDetailsDto.getLat());
                userMatchMapping.setUserLon(userDetailsDto.getLon());
            }
            commonDbService.updateEntity(userMatchMapping);
        }
    }

    private void updateUserMatchCounts(Set<String> userList, int cnt) throws Exception {
        for (String userId : userList) {
            geoHashRedisService.updateCount(GeoHashRedisService.getKeyForUserMatchCount(userId), cnt);
            geoHashRedisService.updateCount(GeoHashRedisService.getKeyForUserDailyMatchCount(userId), cnt);
        }
    }

    @Override
    public UserMatchInfoResponse getUserMatchInfo(String userId) throws Exception {
        UserMatchInfoResponse userMatchInfoResponse = new UserMatchInfoResponse();
        userMatchInfoResponse.setStatus(MPResponseStatus.FAILURE.name());

        BestMatchResponse bestMatchResponse = matchHelperService.createMatchResponseIfExists(userId);
        if (MPResponseStatus.FAILURE.name().equalsIgnoreCase(bestMatchResponse.getStatus())) {
            userMatchInfoResponse.setMessage(bestMatchResponse.getMessage());
            return userMatchInfoResponse;
        }
        MatchInfo matchInfo = matchInfoDao.getMatchInfoFromMatchId(bestMatchResponse.getMatchId());
        if (matchInfo == null) {
            userMatchInfoResponse.setMessage("Match info does not exists");
            return userMatchInfoResponse;
        }
        List<UserMatchMapping> userMatchMappingList = matchInfoDao.getUserMatchMappingForMatchInfoId(matchInfo.getId());
        userMatchInfoResponse.setMatchedUserId(bestMatchResponse.getMatchedUserId());
        userMatchInfoResponse.setMatchTime(matchInfo.getCreateDate());
        userMatchInfoResponse.setMeetingLat(matchInfo.getMeetingLat());
        userMatchInfoResponse.setMeetingLon(matchInfo.getMeetingLon());
        userMatchInfoResponse.setMeetingTime(matchInfo.getMeetingTime());
        userMatchInfoResponse.setMatchStatus(matchInfo.getMatchStatus());
        userMatchInfoResponse.setMatchId(matchInfo.getMatchId());
        for (UserMatchMapping userMatchMapping : userMatchMappingList) {
            if (userId.equalsIgnoreCase(userMatchMapping.getUserId())) {
                userMatchInfoResponse.setUserLatOnMatch(userMatchMapping.getUserLat());
                userMatchInfoResponse.setUserLonOnMatch(userMatchMapping.getUserLon());
            } else {
                userMatchInfoResponse.setMatchedUserLatOnMatch(userMatchMapping.getUserLat());
                userMatchInfoResponse.setMatchedUserLonOnMatch(userMatchMapping.getUserLon());
            }
        }
        userMatchInfoResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return userMatchInfoResponse;
    }
}
