package com.matchmaker.common.dto;

public class BestMatchResponse extends MPResponse {

    private String userId;

    private String matchedUserId;

    private String matchId;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

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
