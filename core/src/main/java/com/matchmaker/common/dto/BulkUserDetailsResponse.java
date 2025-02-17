package com.matchmaker.common.dto;

import java.util.Map;


public class BulkUserDetailsResponse extends MPResponse {

    Map<String, UserDetailsDto> userDetails;

    public Map<String, UserDetailsDto> getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(Map<String, UserDetailsDto> userDetails) {
        this.userDetails = userDetails;
    }
}
