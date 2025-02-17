package com.matchmaker.service.impl;

import com.matchmaker.common.db.service.CommonDbService;
import com.matchmaker.common.dto.MatchInfoAddRequest;
import com.matchmaker.common.dto.MatchInfoAddResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.dto.UserMatchConstraintsDto;
import com.matchmaker.dao.MatchInfoDao;
import com.matchmaker.model.MatchInfo;
import com.matchmaker.model.UserMatchMapping;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.service.MatchInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service("matchInfoServiceImpl")
public class MatchInfoServiceImpl implements MatchInfoService {

    @Autowired
    MatchInfoDao matchInfoDao;

    @Autowired
    CommonDbService commonDbService;

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MatchInfoAddResponse setMatchInfo(MatchInfoAddRequest request) throws Exception {
        MatchInfoAddResponse createMatchResponse = new MatchInfoAddResponse();
        createMatchResponse.setStatus(MPResponseStatus.FAILURE.name());

        String requestId = request.getRequestId();
        MatchInfo matchInfo = matchInfoDao.getMatchInfoFromRequestId(requestId);
        if (matchInfo != null) {
            createMatchResponse.setMatchId(matchInfo.getId());
            createMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
            return createMatchResponse;
        }
        matchInfo = setMatchInfo(request.getMeetingLat(), request.getMeetingLon(), requestId);
        setUserMatchMapping(request.getUsers(), matchInfo.getId());
        updateUserMatchLimits(request.getUsers(), 1);

        createMatchResponse.setMatchId(matchInfo.getId());
        createMatchResponse.setStatus(MPResponseStatus.SUCCESS.name());
        return createMatchResponse;
    }

    private MatchInfo setMatchInfo(Double meetingLat, Double meetingLon, String requestId) throws Exception {
        MatchInfo matchInfo = new MatchInfo();
        matchInfo.setMeetingLat(meetingLat);
        matchInfo.setMeetingLon(meetingLon);
        matchInfo.setRequestId(requestId);
        matchInfo = commonDbService.updateEntity(matchInfo);
        return matchInfo;
    }

    private void setUserMatchMapping(List<String> userList, Long matchId) throws Exception {
        for (String userId : userList) {
            UserMatchMapping userMatchMapping = new UserMatchMapping();
            userMatchMapping.setMatchId(matchId);
            userMatchMapping.setUserId(userId);
            commonDbService.updateEntity(userMatchMapping);
        }
    }

    private void updateUserMatchLimits(List<String> userList, int cnt) throws Exception {
        for (String userId : userList) {
            geoHashRedisService.updateCount(GeoHashRedisService.getKeyForUserMatchLimit(userId), cnt);
            geoHashRedisService.updateCount(GeoHashRedisService.getKeyForUserDailyMatchLimit(userId), cnt);
        }
    }
}
