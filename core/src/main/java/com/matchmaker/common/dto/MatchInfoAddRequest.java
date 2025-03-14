package com.matchmaker.common.dto;

import com.matchmaker.constants.GlobalConstants;

import javax.validation.constraints.*;
import java.util.List;

public class MatchInfoAddRequest {

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private Double meetingLat;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private Double meetingLon;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private List<String> users;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    @NotEmpty(message = "Match ID cannot be empty")
    private String matchId;

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

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }
}
