package com.matchmaker.service;

import com.matchmaker.common.dto.MatchInfoAddRequest;
import com.matchmaker.common.dto.MatchInfoAddResponse;

public interface MatchInfoService {

    MatchInfoAddResponse setMatchInfo(MatchInfoAddRequest createMatchRequest) throws Exception;
}
