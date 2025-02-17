package com.matchmaker.common.dto;

public class BestMatchResponse extends MPResponse {

    private String userId;

    private String matchedUserId;

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
}
