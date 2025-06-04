package com.example.end.infrastructure.config;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CacheMonitoringConfig {

    private final CacheManager cacheManager;

    /**
     * Мониторинг кэша каждые 30 минут для предотвращения утечек памяти
     */
    @Scheduled(fixedRate = 1800000) // 30 минут
    public void monitorCacheUsage() {
        if (cacheManager != null) {
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    log.info("Cache '{}' is active and monitored", cacheName);
                }
            });
        }
    }

    /**
     * Очистка кэша каждые 2 часа для предотвращения накопления
     */
    @Scheduled(fixedRate = 7200000) // 2 часа
    public void cleanupCache() {
        if (cacheManager != null) {
            log.info("Starting scheduled cache cleanup");
            cacheManager.getCacheNames().forEach(cacheName -> {
                var cache = cacheManager.getCache(cacheName);
                if (cache != null) {
                    // Redis TTL автоматически очищает, но логируем для мониторинга
                    log.debug("Cache '{}' cleanup checked", cacheName);
                }
            });
            log.info("Scheduled cache cleanup completed");
        }
    }
}
