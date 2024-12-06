package com.matchmaker.common.dto;


public class FindMatchResponse extends MPResponse {

    private String matchedUserId;

    public String getMatchedUserId() {
        return matchedUserId;
    }

    public void setMatchedUserId(String matchedUserId) {
        this.matchedUserId = matchedUserId;
    }
}
