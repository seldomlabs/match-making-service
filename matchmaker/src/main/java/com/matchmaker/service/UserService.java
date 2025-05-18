package com.matchmaker.service;

import com.matchmaker.common.dto.BulkUserDetailsResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.dto.UserDetailsDto;
import com.matchmaker.constant.ExternalUrls;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.unirest.HttpCallsService;
import com.matchmaker.util.CollectionUtility;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("userService")
public class UserService {

    Logger logger = LogManager.getLogger(UserService.class);

    @Autowired
    GeoHashRedisService geoHashRedisService;

    public UserDetailsDto getUserData(String userId) {
        try {
            URIBuilder url = new URIBuilder(ExternalUrls.GET_USER_DATA_URL);
            url.addParameter("ids", GlobalConstants.objectMapper.writeValueAsString(Collections.singletonList(userId)));
            String response = HttpCallsService.makeGetRequestWithTimeout(url.build().toString(), 500);
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
            url.addParameter("ids", GlobalConstants.objectMapper.writeValueAsString(userList));
            String response = HttpCallsService.makeGetRequestWithTimeout(url.build().toString(), 500);
            BulkUserDetailsResponse bulkUserDetailsResponse = GlobalConstants.objectMapper.readValue(response, BulkUserDetailsResponse.class);
            if (MPResponseStatus.SUCCESS.name().equals(bulkUserDetailsResponse.getStatus())) {
                return bulkUserDetailsResponse.getUserDetails();
            }
        } catch (Exception e) {
            logger.error("Exception in getUserDataBulk", e);
        }
        return new HashMap<>();
    }

    public Map<String, UserDetailsDto> getUserDetails(List<String> userIds) {
        List<List<String>> shardedUserList = CollectionUtility.shardList(userIds, 100);
        Map<String, UserDetailsDto> finalUserDetailsMap = new HashMap<>();
        for (List<String> userList : shardedUserList) {
            Map<String, UserDetailsDto> userDetailsResponse = getUserDataBulk(userList);
            if (userDetailsResponse.isEmpty()) {
                continue;
            }
            finalUserDetailsMap.putAll(userDetailsResponse);
        }
        return finalUserDetailsMap;
    }

    public Map<String, UserDetailsDto> fetchUserLocation(List<String> userList) {
        int len = userList.size();
        String[] keys = new String[len];
        for (int i = 0; i < len; i++) {
            keys[i] = GeoHashRedisService.getKeyForUserLocation(userList.get(i));
        }
        Map<String, UserDetailsDto> userDetailsMap = new HashMap<>();
        try {
            List<String> userDetailsString = geoHashRedisService.getDataForKeys(keys);
            for (int i = 0; i < len; i++) {
                String userId = userList.get(i);
                if (userDetailsString.get(i) != null) {
                    userDetailsMap.put(userId, GlobalConstants.objectMapper.readValue(userDetailsString.get(i), UserDetailsDto.class));
                }
            }
        } catch (Exception e) {
            logger.error("Exception while getting user's location data", e);
        }
        return userDetailsMap;
    }
}
