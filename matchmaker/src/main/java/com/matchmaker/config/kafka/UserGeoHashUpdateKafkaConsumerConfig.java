package com.matchmaker.config.kafka;

import com.matchmaker.util.ApplicationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

@EnableKafka
@Configuration
public class UserGeoHashUpdateKafkaConsumerConfig extends AbstractKafkaConsumerConfig {

    private static final String KAFKA_USER_GEO_HASH_UPDATE_GROUP_ID = ApplicationProperties.getInstance().getProperty("kafka",
            "kafka.userGeoHashUpdate.group", "user_geo_hash_update_group");

    private static final String KAFKA_USER_GEO_HASH_UPDATE_CONCURRENCY = ApplicationProperties.getInstance().getProperty("kafka",
            "kafka.userGeoHashUpdate.concurrency", "0");

    @Override
    public String groupId() {
        return KAFKA_USER_GEO_HASH_UPDATE_GROUP_ID;
    }

    @Override
    public String getKafkaConcurrency() { return KAFKA_USER_GEO_HASH_UPDATE_CONCURRENCY;}

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Integer, String> geoHashKafkaListenerContainerFactory() {
        return createContainerFactory();
    }
}
