package com.restaurant.menu.config;

import com.restaurant.menu.service.DishCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 缓存初始化类
 * 用于系统启动时预热缓存
 */
@Configuration
@EnableAsync
public class CacheInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(CacheInitializer.class);

    @Autowired
    private DishCacheService dishCacheService;

    @Override
    public void run(String... args) {
        // 异步预热菜品缓存，避免阻塞启动
        preloadCacheAsync();
    }
    
    @Async
    public void preloadCacheAsync() {
        try {
            logger.info("开始预热菜品缓存...");
            long startTime = System.currentTimeMillis();
            
            dishCacheService.preloadDishCache();
            
            long endTime = System.currentTimeMillis();
            logger.info("菜品缓存预热完成，耗时: {}ms", endTime - startTime);
        } catch (Exception e) {
            logger.error("菜品缓存预热失败", e);
        }
    }
} 