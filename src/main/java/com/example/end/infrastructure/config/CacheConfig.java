package com.example.end.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
public class CacheConfig {

        @Value("${spring.cache.redis.time-to-live:3600000}")
        private long defaultTtl;

        @Bean
        public RedisCacheManagerBuilderCustomizer redisCacheManagerBuilderCustomizer() {
                return builder -> {
               
                        var categoryConfig = RedisCacheConfiguration.defaultCacheConfig()
                                        .entryTtl(Duration.ofMinutes(15))
                                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

                        var procedureConfig = RedisCacheConfiguration.defaultCacheConfig()
                                        .entryTtl(Duration.ofMinutes(10))
                                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

                        var defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                                        .entryTtl(Duration.ofMillis(defaultTtl))
                                        .serializeValuesWith(RedisSerializationContext.SerializationPair
                                                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));
                        var mastersConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(15))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

                        var usersByCategoryConfig = RedisCacheConfiguration.defaultCacheConfig()
                                .entryTtl(Duration.ofMinutes(15))
                                .serializeValuesWith(RedisSerializationContext.SerializationPair
                                        .fromSerializer(new GenericJackson2JsonRedisSerializer()));

                        builder
                                        .withCacheConfiguration("allMasters", mastersConfig)
                                        .withCacheConfiguration("usersByCategory", usersByCategoryConfig)
                                        .withCacheConfiguration("allCategories", categoryConfig)
                                        .withCacheConfiguration("category", categoryConfig)
                                        .withCacheConfiguration("allProcedures", procedureConfig)
                                        .withCacheConfiguration("procedure", procedureConfig)
                                        .withCacheConfiguration("proceduresByCategory", procedureConfig)
                                        .cacheDefaults(defaultConfig);
                };
        }
}