package com.matchmaker.service;

import com.matchmaker.common.dto.BulkUserDetailsResponse;
import com.matchmaker.common.dto.MPResponseStatus;
import com.matchmaker.common.dto.UserDetailsDto;
import com.matchmaker.common.dto.UserSubscriptionDetailsResponse;
import com.matchmaker.constant.ExternalUrls;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.unirest.HttpCallsService;
import com.matchmaker.util.DateConvertUtils;
import org.apache.http.client.utils.URIBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class SubscriptionService {

    Logger logger = LogManager.getLogger(SubscriptionService.class);

    public UserSubscriptionDetailsResponse getUserSubscriptionDetails(String userId) {
        try {
            String url = ExternalUrls.GET_USER_SUBSCRIPTION_DATA_URL + "/" + userId;
            String response = HttpCallsService.makeGetRequestWithTimeout(url, 500);
            return GlobalConstants.objectMapper.readValue(response, UserSubscriptionDetailsResponse.class);
        } catch (Exception e) {
            logger.error("Exception in getUserSubscriptionDetails", e);
        }
        return new UserSubscriptionDetailsResponse();
    }
}
