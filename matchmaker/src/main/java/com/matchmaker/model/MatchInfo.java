package com.matchmaker.model;

import com.matchmaker.common.db.domain.AbstractJpaEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "match_info")
public class MatchInfo extends AbstractJpaEntity {

    @Column(name = "match_status")
    private String matchStatus;

    @Column(name = "meeting_lat")
    private Double meetingLat;

    @Column(name = "meeting_lon")
    private Double meetingLon;

    @Column(name = "match_id")
    private String matchId;

    @Column(name = "meeting_time")
    private Date meetingTime;

    public String getMatchStatus() {
        return matchStatus;
    }

    public void setMatchStatus(String matchStatus) {
        this.matchStatus = matchStatus;
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

    public String getMatchId() {
        return matchId;
    }

    public void setMatchId(String matchId) {
        this.matchId = matchId;
    }

    public Date getMeetingTime() {
        return meetingTime;
    }

    public void setMeetingTime(Date meetingTime) {
        this.meetingTime = meetingTime;
    }
}
