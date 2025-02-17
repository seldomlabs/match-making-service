package com.matchmaker.service;


import com.matchmaker.common.dto.BestMatchRequestDto;
import com.matchmaker.common.dto.BestMatchResponse;

public interface MatchStrategy {

    BestMatchResponse findBestMatch(BestMatchRequestDto bestMatchRequestDto);
}
