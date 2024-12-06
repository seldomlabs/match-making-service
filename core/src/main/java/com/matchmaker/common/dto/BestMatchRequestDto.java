package com.matchmaker.common.dto;


import java.util.List;

public class BestMatchRequestDto {

    private List<String> activeUsers;

    private Double userLat;

    private Double userLon;

    private String userIdToMatch;

    public List<String> getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(List<String> activeUsers) {
        this.activeUsers = activeUsers;
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

    public BestMatchRequestDto(BestMatchRequestDtoBuilder bestMatchRequestDtoBuilder) {
        this.activeUsers = bestMatchRequestDtoBuilder.activeUsers;
        this.userLat = bestMatchRequestDtoBuilder.userLat;
        this.userLon = bestMatchRequestDtoBuilder.userLon;
        this.userIdToMatch = bestMatchRequestDtoBuilder.userIdToMatch;
    }

    public static class BestMatchRequestDtoBuilder {
        private List<String> activeUsers;
        private Double userLat;
        private Double userLon;
        private String userIdToMatch;

        public BestMatchRequestDtoBuilder activeUsers(List<String> activeUsers) {
            this.activeUsers = activeUsers;
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

        public BestMatchRequestDto build(){
            return new BestMatchRequestDto(this);
        }
    }
}
