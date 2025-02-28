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
    @NotEmpty(message = "Request ID cannot be empty")
    private String requestId;

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

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public MatchInfoAddRequest(MatchInfoAddRequest.CreateMatchRequestBuilder createMatchRequestBuilder) {
        this.meetingLat = createMatchRequestBuilder.meetingLat;
        this.meetingLon = createMatchRequestBuilder.meetingLon;
        this.users = createMatchRequestBuilder.users;
        this.requestId = createMatchRequestBuilder.requestId;
    }

    public static class CreateMatchRequestBuilder {
        private Double meetingLat;
        private Double meetingLon;
        private String requestId;
        private List<String> users;

        public CreateMatchRequestBuilder meetingLat(Double meetingLat) {
            this.meetingLat = meetingLat;
            return this;
        }

        public CreateMatchRequestBuilder meetingLon(Double meetingLon) {
            this.meetingLon = meetingLon;
            return this;
        }

        public CreateMatchRequestBuilder users(List<String> users) {
            this.users = users;
            return this;
        }

        public CreateMatchRequestBuilder requestId(String requestId) {
            this.requestId = requestId;
            return this;
        }

        public MatchInfoAddRequest build(){
            return new MatchInfoAddRequest(this);
        }
    }
}
