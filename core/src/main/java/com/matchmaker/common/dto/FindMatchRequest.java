package com.matchmaker.common.dto;

import com.matchmaker.constants.GlobalConstants;

import javax.validation.constraints.NotNull;

public class FindMatchRequest {

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private String userId;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private Double lat;

    @NotNull(message = GlobalConstants.ValidationMessages.DATA_INVALID)
    private Double lon;

    private Integer matchRadius;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double getLon() {
        return lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Integer getMatchRadius() {
        return matchRadius;
    }

    public void setMatchRadius(Integer matchRadius) {
        this.matchRadius = matchRadius;
    }
}
