package com.matchmaker.service;

import com.matchmaker.common.dto.BulkUserDetailsResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.dto.UserDetailsDto;
import com.matchmaker.constant.ExternalUrls;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.unirest.HttpCallsService;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class UserService {

    Logger logger = LogManager.getLogger(UserService.class);

    public UserDetailsDto getUserData(String userId) {
        try {
            URIBuilder url = new URIBuilder(ExternalUrls.GET_USER_DATA_URL);
            url.addParameter("users", GlobalConstants.objectMapper.writeValueAsString(Collections.singletonList(userId)));
            String response = HttpCallsService.makeGetRequestWithTimeout(url.toString(), 500);
            BulkUserDetailsResponse bulkUserDetailsResponse = GlobalConstants.objectMapper.readValue(response, BulkUserDetailsResponse.class);
            if (MPResponseStatus.SUCCESS.name().equals(bulkUserDetailsResponse.getStatus())) {
                Map<String, UserDetailsDto> userDetailsMap = bulkUserDetailsResponse.getUserDetails();
                if (userDetailsMap != null && userDetailsMap.containsKey(userId)) {
                    return userDetailsMap.get(userId);
                }
            }
        } catch (Exception e) {
            logger.error("Exception in getUserData", e);
        }
        return null;
    }

    public Map<String, UserDetailsDto> getUserDataBulk(List<String> userList) {
        try {
            URIBuilder url = new URIBuilder(ExternalUrls.GET_USER_DATA_URL);
            url.addParameter("users", GlobalConstants.objectMapper.writeValueAsString(userList));
            String response = HttpCallsService.makeGetRequestWithTimeout(url.toString(), 500);
            BulkUserDetailsResponse bulkUserDetailsResponse = GlobalConstants.objectMapper.readValue(response, BulkUserDetailsResponse.class);
            if (MPResponseStatus.SUCCESS.name().equals(bulkUserDetailsResponse.getStatus())) {
                return bulkUserDetailsResponse.getUserDetails();
            }
        } catch (Exception e) {
            logger.error("Exception in getUserDataBulk", e);
        }
        return new HashMap<>();
    }
}
