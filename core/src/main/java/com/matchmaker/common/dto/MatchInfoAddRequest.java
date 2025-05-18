package com.matchmaker.common.dto;

import com.matchmaker.common.EnumValue;
import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.constants.GlobalConstants;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;

public class MatchInfoAddRequest {

    private Double meetingLat;

    private Double meetingLon;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private Set<String> users;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    @NotEmpty(message = "Match ID cannot be empty")
    private String matchId;

    @EnumValue(enumClass = MatchmakingConstants.MatchStatus.class, message = "Invalid status. Must be PENDING, CONFIRMED, CANCELED, MET.")
    private String matchStatus;

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

    public Set<String> getUsers() {
        return users;
    }

    public void setUsers(Set<String> users) {
        this.users = users;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
    }
}
