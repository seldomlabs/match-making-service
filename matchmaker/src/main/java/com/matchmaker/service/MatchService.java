package com.matchmaker.service;

import com.matchmaker.common.dto.FindMatchRequest;
import com.matchmaker.common.dto.FindMatchResponse;


public interface MatchService {

    FindMatchResponse findMatchForUser(FindMatchRequest request) throws Exception;
}
