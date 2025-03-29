package com.matchmaker.service.impl;

import com.matchmaker.common.constants.MatchmakingConstants.MatchStatus;
import com.matchmaker.common.db.service.CommonDbService;
import com.matchmaker.common.dto.MatchInfoAddRequest;
import com.matchmaker.common.dto.MatchInfoAddResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.dto.UpdateMatchInfoRequest;
import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.model.MatchInfo;
import com.matchmaker.model.UserMatchMapping;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.service.MatchHelperService;
import com.matchmaker.service.MatchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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
        matchHelperService.removeUsersMatchIdKeyFromRedis(request.getUsers());

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
        for (String userId : userList) {
            UserMatchMapping userMatchMapping = new UserMatchMapping();
            userMatchMapping.setMatchInfoId(matchInfoId);
            userMatchMapping.setUserId(userId);
            commonDbService.updateEntity(userMatchMapping);
        }
    }

    private void updateUserMatchCounts(Set<String> userList, int cnt) throws Exception {
        for (String userId : userList) {
            geoHashRedisService.updateCount(GeoHashRedisService.getKeyForUserMatchCount(userId), cnt);
            geoHashRedisService.updateCount(GeoHashRedisService.getKeyForUserDailyMatchCount(userId), cnt);
        }
    }
}
