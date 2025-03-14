package com.matchmaker.common.dto;


public class FindMatchResponse extends MPResponse {

    private String matchedUserId;

    private String matchId;

    public String getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(String matchedUserId) {
        this.matchedUserId = matchedUserId;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
