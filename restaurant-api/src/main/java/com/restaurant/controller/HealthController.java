package com.restaurant.controller;

import com.restaurant.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查控制器
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 基础健康检查
     */
    @GetMapping
    public Result<String> health() {
        return Result.success("餐厅点餐系统运行正常");
    }

    /**
     * 详细健康检查
     */
    @GetMapping("/detail")
    public Result<Map<String, Object>> healthDetail() {
        Map<String, Object> status = new HashMap<>();

        // 检查数据库连接
        try (Connection connection = dataSource.getConnection()) {
            status.put("database", connection.isValid(5) ? "UP" : "DOWN");
        } catch (Exception e) {
            status.put("database", "DOWN");
            status.put("database_error", e.getMessage());
        }

        // 检查Redis连接
        try {
            // 使用简单的ping命令测试Redis连接
            redisTemplate.getConnectionFactory().getConnection().ping();
            status.put("redis", "UP");
        } catch (Exception e) {
            status.put("redis", "DOWN");
            status.put("redis_error", e.getMessage());
        }

        // 如果Redis连接有问题，可以临时注释上面的代码，使用下面的代码：
        // status.put("redis", "SKIPPED");

        status.put("application", "UP");
        status.put("timestamp", System.currentTimeMillis());

        return Result.success("系统状态检查完成", status);
    }
}