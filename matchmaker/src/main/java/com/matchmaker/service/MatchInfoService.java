package com.matchmaker.service;

import com.matchmaker.common.dto.MatchInfoAddRequest;
import com.matchmaker.common.dto.MatchInfoAddResponse;
import com.matchmaker.common.dto.UpdateMatchInfoRequest;
import com.matchmaker.common.dto.UserMatchInfoResponse;

public interface MatchInfoService {

    MatchInfoAddResponse setMatchInfo(MatchInfoAddRequest createMatchRequest) throws Exception;

    MatchInfoAddResponse updateMatchInfo(UpdateMatchInfoRequest createMatchRequest) throws Exception;

    UserMatchInfoResponse getUserMatchInfo(String userId) throws Exception;
}
