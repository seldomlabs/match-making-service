package com.matchmaker.common.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

public class UserSubscriptionDetailsResponse extends MPResponse {

    @JsonProperty("user_id")
    private String userId;

    private String status;

    @JsonProperty("subscription_start_date")
    private Date subscriptionStartDate;

    @JsonProperty("subscription_end_date")
    private Date subscriptionEndDate;

    @JsonProperty("daily_match_limits")
    private Integer dailyMatchLimits;

    @JsonProperty("total_match_limits")
    private Integer totalMatchLimits;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getSubscriptionStartDate() {
        return subscriptionStartDate;
    }

    public void setSubscriptionStartDate(Date subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }

    public Date getSubscriptionEndDate() {
        return subscriptionEndDate;
    }

    public void setSubscriptionEndDate(Date subscriptionEndDate) {
        this.subscriptionEndDate = subscriptionEndDate;
    }

    public Integer getDailyMatchLimits() {
        return dailyMatchLimits;
    }

    public void setDailyMatchLimits(Integer dailyMatchLimits) {
        this.dailyMatchLimits = dailyMatchLimits;
    }

    public Integer getTotalMatchLimits() {
        return totalMatchLimits;
    }

    public void setTotalMatchLimits(Integer totalMatchLimits) {
        this.totalMatchLimits = totalMatchLimits;
    }
}
