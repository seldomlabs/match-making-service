package com.matchmaker.service;


import com.matchmaker.common.dto.BestMatchRequestDto;

public interface MatchStrategy {

    String findBestMatch(BestMatchRequestDto bestMatchRequestDto);
}
