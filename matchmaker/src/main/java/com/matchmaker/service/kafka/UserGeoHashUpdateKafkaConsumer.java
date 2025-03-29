package com.matchmaker.service.kafka;

import com.matchmaker.common.constants.MatchmakingConstants;
import com.matchmaker.common.dto.UserDetailsDto;
import com.matchmaker.common.dto.UserGeoHashUpdateEventDto;
import com.matchmaker.constants.GlobalConstants;
import com.matchmaker.service.GeoHashRedisService;
import com.matchmaker.util.H3Utility;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;


@Service("userGeoHashUpdateKafkaConsumer")
public class UserGeoHashUpdateKafkaConsumer {

    Logger logger = LogManager.getLogger(UserGeoHashUpdateKafkaConsumer.class);

    @Autowired
    GeoHashRedisService geoHashRedisService;

    @KafkaListener(topics = "geo_hash_update", containerFactory = "geoHashKafkaListenerContainerFactory")
    public void userGeoHashUpdate(String message, Acknowledgment acknowledgment) {
        try {
            logger.info("request" + message);
            UserGeoHashUpdateEventDto request = GlobalConstants.objectMapper.readValue(message, UserGeoHashUpdateEventDto.class);
            String operation = request.getOperation();
            String userId = request.getUserId();
            if ("add".equalsIgnoreCase(operation)) {
                setUserLocationInRedis(request.getUserId(), request.getLat(), request.getLon());
                removeUserFromActiveGeoHashSet(userId);
                addUserToActiveUserGeoHashSet(userId, request.getLat(), request.getLon());
            } else if ("remove".equalsIgnoreCase(operation)) {
                removeUserFromActiveGeoHashSet(userId);
                geoHashRedisService.deleteKey(GeoHashRedisService.getKeyForUserLocation(userId));
            }
        } catch (Exception e) {
            logger.error("Exception in userGeoHashUpdate", e);
        }
        acknowledgment.acknowledge();
    }

    private void setUserLocationInRedis(String userId, Double lat, Double lon) throws Exception{
        UserDetailsDto userDetails = new UserDetailsDto();
        userDetails.setLat(lat);
        userDetails.setLon(lon);
        userDetails.setUserId(userId);
        logger.info("user details " + GlobalConstants.objectMapper.writeValueAsString(userDetails));
        geoHashRedisService.setKey(GeoHashRedisService.getKeyForUserLocation(userId), GlobalConstants.objectMapper.writeValueAsString(userDetails));
    }

    private void addUserToActiveUserGeoHashSet(String userId, Double lat, Double lon) throws Exception {
        String geoHashUser = H3Utility.latLonToH3(lat, lon, MatchmakingConstants.H3_RESOLUTION);
        geoHashRedisService.addMemberToSet(GeoHashRedisService.getKeyForActiveUsersGeoHashSet(geoHashUser), userId);
        geoHashRedisService.setKey(GeoHashRedisService.getKeyForUserGeoHashMapping(userId), geoHashUser);
    }

    private void removeUserFromActiveGeoHashSet(String userId) throws Exception {
        String geoHashUser = geoHashRedisService.getKey(GeoHashRedisService.getKeyForUserGeoHashMapping(userId));
        geoHashRedisService.removeMemberFromSet(GeoHashRedisService.getKeyForActiveUsersGeoHashSet(geoHashUser), userId);
        geoHashRedisService.deleteKey(GeoHashRedisService.getKeyForUserGeoHashMapping(userId));
    }
}
