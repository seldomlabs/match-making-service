package com.matchmaker.common.dto;

public class UserMatchConstraintsDto {

    private Integer matchLimit;

    private Integer matchLimitPerDay;

    public Integer getMatchLimit() {
        return matchLimit;
    }

    public void setMatchLimit(Integer matchLimit) {
        this.matchLimit = matchLimit;
    }

    public Integer getMatchLimitPerDay() {
        return matchLimitPerDay;
    }

    public void setMatchLimitPerDay(Integer matchLimitPerDay) {
        this.matchLimitPerDay = matchLimitPerDay;
    }
}
