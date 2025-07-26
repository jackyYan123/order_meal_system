package com.restaurant.menu.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * 缓存同步配置类
 * 用于配置缓存过期时间和同步策略
 */
@Configuration
@EnableCaching
public class CacheSyncConfig {

    // 定义缓存名称常量
    public static final String DISH_CACHE = "dishCache";
    public static final String DISH_LIST_CACHE = "dishListCache";
    public static final String DISH_CATEGORY_CACHE = "dishCategoryCache";

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        // 默认缓存配置
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // 默认30分钟过期
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues(); // 不缓存空值

        // 特定缓存配置
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // 菜品缓存：60分钟过期
        cacheConfigurations.put(DISH_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(60)));
        
        // 菜品列表缓存：30分钟过期
        cacheConfigurations.put(DISH_LIST_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        
        // 分类菜品缓存：30分钟过期
        cacheConfigurations.put(DISH_CATEGORY_CACHE, defaultConfig.entryTtl(Duration.ofMinutes(30)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
} 