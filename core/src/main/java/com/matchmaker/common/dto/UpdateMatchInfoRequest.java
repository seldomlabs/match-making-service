package com.matchmaker.common.dto;

import com.matchmaker.common.EnumValue;
import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.constants.GlobalConstants;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

public class UpdateMatchInfoRequest {

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    @NotEmpty(message = "Match ID cannot be empty")
    private String matchId;

    private Double meetingLat;

    private Double meetingLon;

    @EnumValue(enumClass = MatchmakingConstants.MatchStatus.class, message = "Invalid status. Must be PENDING, CONFIRMED, CANCELED, MET.")
    private String matchStatus;

    private Date meetingTime;

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Double getMeetingLat() {
        return meetingLat;
    }

    public void setMeetingLat(Double meetingLat) {
        this.meetingLat = meetingLat;
    }

    public Double getMeetingLon() {
        return meetingLon;
    }

    public void setMeetingLon(Double meetingLon) {
        this.meetingLon = meetingLon;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }
}
