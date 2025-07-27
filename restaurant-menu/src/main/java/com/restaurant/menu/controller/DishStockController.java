package com.restaurant.menu.controller;

import com.restaurant.common.result.Result;
import com.restaurant.menu.service.DishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 菜品库存管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/dishes/stock")
@RequiredArgsConstructor
public class DishStockController {
    
    private final DishService dishService;
    
    /**
     * 检查库存是否充足
     */
    @GetMapping("/check/{dishId}")
    public Result<Boolean> checkStock(@PathVariable Long dishId, @RequestParam Integer quantity) {
        log.info("检查菜品库存，菜品ID: {}, 需要数量: {}", dishId, quantity);
        boolean sufficient = dishService.checkStock(dishId, quantity);
        return Result.success(sufficient);
    }
    
    /**
     * 减少库存
     */
    @PutMapping("/reduce/{dishId}")
    public Result<Void> reduceStock(@PathVariable Long dishId, @RequestParam Integer quantity) {
        log.info("减少菜品库存请求，菜品ID: {}, 减少数量: {}", dishId, quantity);
        dishService.reduceStock(dishId, quantity);
        return Result.success();
    }
    
    /**
     * 增加库存
     */
    @PutMapping("/increase/{dishId}")
    public Result<Void> increaseStock(@PathVariable Long dishId, @RequestParam Integer quantity) {
        log.info("增加菜品库存请求，菜品ID: {}, 增加数量: {}", dishId, quantity);
        dishService.increaseStock(dishId, quantity);
        return Result.success();
    }
    
    /**
     * 恢复库存（取消订单时使用）
     */
    @PutMapping("/restore/{dishId}")
    public Result<Void> restoreStock(@PathVariable Long dishId, @RequestParam Integer quantity) {
        log.info("恢复菜品库存请求，菜品ID: {}, 恢复数量: {}", dishId, quantity);
        dishService.restoreStock(dishId, quantity);
        return Result.success();
    }
}