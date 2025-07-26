package com.restaurant.menu.controller;

import com.restaurant.common.result.Result;
import com.restaurant.menu.dto.CreateDishRequest;
import com.restaurant.menu.dto.DishDto;
import com.restaurant.menu.dto.UpdateDishRequest;
import com.restaurant.menu.dto.UpdateStockRequest;
import com.restaurant.menu.service.DishService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜品管理控制器
 */
@RestController
@RequestMapping("/api/dishes")
public class DishController {

    @Autowired
    private DishService dishService;

    /**
     * 获取所有菜品列表
     */
    @GetMapping
    public Result<List<DishDto>> getAllDishes() {
        List<DishDto> dishes = dishService.getAllDishes();
        return Result.success(dishes);
    }

    /**
     * 获取可用的菜品列表
     */
    @GetMapping("/available")
    public Result<List<DishDto>> getAvailableDishes() {
        List<DishDto> dishes = dishService.getAvailableDishes();
        return Result.success(dishes);
    }

    /**
     * 根据分类ID获取菜品列表
     */
    @GetMapping("/category/{categoryId}")
    public Result<List<DishDto>> getDishesByCategory(@PathVariable Long categoryId) {
        List<DishDto> dishes = dishService.getDishesByCategory(categoryId);
        return Result.success(dishes);
    }

    /**
     * 根据分类ID获取可用的菜品列表
     */
    @GetMapping("/category/{categoryId}/available")
    public Result<List<DishDto>> getAvailableDishesByCategory(@PathVariable Long categoryId) {
        List<DishDto> dishes = dishService.getAvailableDishesByCategory(categoryId);
        return Result.success(dishes);
    }

    /**
     * 根据ID获取菜品详情
     */
    @GetMapping("/{id}")
    public Result<DishDto> getDishById(@PathVariable Long id) {
        DishDto dish = dishService.getDishById(id);
        return Result.success(dish);
    }

    /**
     * 创建菜品
     */
    @PostMapping
    public Result<DishDto> createDish(@Valid @RequestBody CreateDishRequest request) {
        DishDto dish = dishService.createDish(request);
        return Result.success(dish);
    }

    /**
     * 更新菜品
     */
    @PutMapping("/{id}")
    public Result<DishDto> updateDish(@PathVariable Long id, @Valid @RequestBody UpdateDishRequest request) {
        DishDto dish = dishService.updateDish(id, request);
        return Result.success(dish);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteDish(@PathVariable Long id) {
        dishService.deleteDish(id);
        return Result.success();
    }

    /**
     * 启用/禁用菜品
     */
    @PutMapping("/{id}/status")
    public Result<Void> toggleDishStatus(@PathVariable Long id, @RequestParam Boolean isAvailable) {
        dishService.toggleDishStatus(id, isAvailable);
        return Result.success();
    }

    /**
     * 更新菜品库存
     */
    @PutMapping("/{id}/stock")
    public Result<Void> updateStock(@PathVariable Long id, @Valid @RequestBody UpdateStockRequest request) {
        dishService.updateStock(id, request);
        return Result.success();
    }

    /**
     * 批量更新库存
     */
    @PutMapping("/batch/stock")
    public Result<Void> batchUpdateStock(@RequestParam List<Long> dishIds, @RequestParam Integer stock) {
        dishService.batchUpdateStock(dishIds, stock);
        return Result.success();
    }

    /**
     * 调整菜品排序
     */
    @PutMapping("/{id}/sort")
    public Result<Void> updateDishSort(@PathVariable Long id, @RequestParam Integer sortOrder) {
        dishService.updateDishSort(id, sortOrder);
        return Result.success();
    }

    /**
     * 上传菜品图片
     */
    @PostMapping("/upload")
    public Result<String> uploadDishImage(@RequestParam("file") MultipartFile file) {
        String imageUrl = dishService.uploadDishImage(file);
        return Result.success(imageUrl);
    }

    /**
     * 搜索菜品
     */
    @GetMapping("/search")
    public Result<List<DishDto>> searchDishes(@RequestParam String keyword) {
        List<DishDto> dishes = dishService.searchDishes(keyword);
        return Result.success(dishes);
    }

    /**
     * 检查菜品库存
     */
    @GetMapping("/{id}/stock/check")
    public Result<Boolean> checkStock(@PathVariable Long id, @RequestParam Integer quantity) {
        boolean hasStock = dishService.checkStock(id, quantity);
        return Result.success(hasStock);
    }

    /**
     * 减少库存（内部接口，供订单服务调用）
     */
    @PutMapping("/{id}/stock/reduce")
    public Result<Void> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        dishService.reduceStock(id, quantity);
        return Result.success();
    }

    /**
     * 增加库存（内部接口，供订单服务调用）
     */
    @PutMapping("/{id}/stock/increase")
    public Result<Void> increaseStock(@PathVariable Long id, @RequestParam Integer quantity) {
        dishService.increaseStock(id, quantity);
        return Result.success();
    }

    /**
     * 获取推荐菜品
     */
    @GetMapping("/recommended")
    public Result<List<DishDto>> getRecommendedDishes() {
        List<DishDto> dishes = dishService.getRecommendedDishes();
        return Result.success(dishes);
    }

    /**
     * 获取热门菜品
     */
    @GetMapping("/popular")
    public Result<List<DishDto>> getPopularDishes() {
        List<DishDto> dishes = dishService.getPopularDishes();
        return Result.success(dishes);
    }
}