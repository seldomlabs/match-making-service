package com.matchmaker.common.dto;


import java.util.List;
import java.util.Map;
import java.util.Set;

public class BestMatchRequestDto {

    // active users in radius r of userIdToMatch
    private List<String> activeUsersInRadius;

    private Double userLat;

    private Double userLon;

    private String userIdToMatch;

    private String userGeoHash;


    public List<String> getActiveUsersInRadius() {
        return activeUsersInRadius;
    }

    public void setActiveUsersInRadius(List<String> activeUsersInRadius) {
        this.activeUsersInRadius = activeUsersInRadius;
    }

    public Double getUserLat() {
        return userLat;
    }

    public void setUserLat(Double userLat) {
        this.userLat = userLat;
    }

    public Double getUserLon() {
        return userLon;
    }

    public void setUserLon(Double userLon) {
        this.userLon = userLon;
    }

    public String getUserIdToMatch() {
        return userIdToMatch;
    }

    public void setUserIdToMatch(String userIdToMatch) {
        this.userIdToMatch = userIdToMatch;
    }

    public String getUserGeoHash() {
        return userGeoHash;
    }

    public void setUserGeoHash(String userGeoHash) {
        this.userGeoHash = userGeoHash;
    }

    public BestMatchRequestDto(BestMatchRequestDtoBuilder bestMatchRequestDtoBuilder) {
        this.activeUsersInRadius = bestMatchRequestDtoBuilder.activeUsersInRadius;
        this.userLat = bestMatchRequestDtoBuilder.userLat;
        this.userLon = bestMatchRequestDtoBuilder.userLon;
        this.userIdToMatch = bestMatchRequestDtoBuilder.userIdToMatch;
    }

    public static class BestMatchRequestDtoBuilder {
        private List<String> activeUsersInRadius;
        private Double userLat;
        private Double userLon;
        private String userIdToMatch;
        private Map<String, Set<String>> activeUsersMap;
        private String userGeoHash;

        public BestMatchRequestDtoBuilder activeUsersInRadius(List<String> activeUsersInRadius) {
            this.activeUsersInRadius = activeUsersInRadius;
            return this;
        }

        public BestMatchRequestDtoBuilder userLat(Double userLat) {
            this.userLat = userLat;
            return this;
        }

        public BestMatchRequestDtoBuilder userLon(Double userLon) {
            this.userLon = userLon;
            return this;
        }

        public BestMatchRequestDtoBuilder userIdToMatch(String userIdToMatch) {
            this.userIdToMatch = userIdToMatch;
            return this;
        }

        public BestMatchRequestDtoBuilder userGeoHash(String userGeoHash) {
            this.userGeoHash = userGeoHash;
            return this;
        }

        public BestMatchRequestDto build(){
            return new BestMatchRequestDto(this);
        }
    }
}
