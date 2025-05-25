package com.matchmaker.common.dto;

import java.util.Date;

public class UserMatchInfoResponse extends MPResponse{

    private String userId;

    private String matchedUserId;

    private String matchId;

    private Double meetingLat;

    private Double meetingLon;

    private Date meetingTime;

    private Date matchTime;

    private String matchStatus;

    private Double userLatOnMatch;

    private Double userLonOnMatch;

    private Double matchedUserLatOnMatch;

    private Double matchedUserLonOnMatch;

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

    public Double getMeetingLon() {
        return meetingLon;
    }

    public void setMeetingLon(Double meetingLon) {
        this.meetingLon = meetingLon;
    }

    public Double getMeetingLat() {
        return meetingLat;
    }

    public void setMeetingLat(Double meetingLat) {
        this.meetingLat = meetingLat;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }

    public Date getMatchTime() {
        return matchTime;
    }

    public void setMatchTime(Date matchTime) {
        this.matchTime = matchTime;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Double getUserLatOnMatch() {
        return userLatOnMatch;
    }

    public void setUserLatOnMatch(Double userLatOnMatch) {
        this.userLatOnMatch = userLatOnMatch;
    }

    public Double getUserLonOnMatch() {
        return userLonOnMatch;
    }

    public void setUserLonOnMatch(Double userLonOnMatch) {
        this.userLonOnMatch = userLonOnMatch;
    }

    public Double getMatchedUserLatOnMatch() {
        return matchedUserLatOnMatch;
    }

    public void setMatchedUserLatOnMatch(Double matchedUserLatOnMatch) {
        this.matchedUserLatOnMatch = matchedUserLatOnMatch;
    }

    public Double getMatchedUserLonOnMatch() {
        return matchedUserLonOnMatch;
    }

    public void setMatchedUserLonOnMatch(Double matchedUserLonOnMatch) {
        this.matchedUserLonOnMatch = matchedUserLonOnMatch;
    }
}
