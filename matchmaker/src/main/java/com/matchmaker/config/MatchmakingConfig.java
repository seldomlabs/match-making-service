package com.matchmaker.config;

import com.matchmaker.constant.MatchStrategies;
import com.matchmaker.service.MatchStrategy;
import com.matchmaker.service.impl.DistanceMatchStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class MatchmakingConfig {

    @Bean
    public Map<String, MatchStrategy> getMatchmakingStrategies(DistanceMatchStrategy distanceMatchmakingStrategy) {
        Map<String, MatchStrategy> mathmakingStrategyMap = new HashMap<>();
        mathmakingStrategyMap.put(MatchStrategies.distance.name(), distanceMatchmakingStrategy);
        return mathmakingStrategyMap;
    }
}
