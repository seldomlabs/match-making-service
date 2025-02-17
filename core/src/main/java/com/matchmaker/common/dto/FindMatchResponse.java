package com.matchmaker.common.dto;


public class FindMatchResponse extends MPResponse {

    private String matchedUserId;

    private Long matchId;

    public String getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(String matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    public Long getMatchId() {
        return matchId;
    }

    public void setMatchId(Long matchId) {
        this.matchId = matchId;
    }
}
