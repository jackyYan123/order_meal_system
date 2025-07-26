package com.restaurant.menu.service;

import com.restaurant.menu.dto.DishDto;
import com.restaurant.menu.entity.Dish;

import java.util.List;

/**
 * 菜品缓存服务接口
 */
public interface DishCacheService {
    
    /**
     * 获取所有菜品的缓存
     */
    List<DishDto> getAllDishesFromCache();
    
    /**
     * 获取可用菜品的缓存
     */
    List<DishDto> getAvailableDishesFromCache();
    
    /**
     * 根据分类ID获取菜品的缓存
     */
    List<DishDto> getDishesByCategoryFromCache(Long categoryId);
    
    /**
     * 根据分类ID获取可用菜品的缓存
     */
    List<DishDto> getAvailableDishesByCategoryFromCache(Long categoryId);
    
    /**
     * 根据ID获取菜品的缓存
     */
    DishDto getDishByIdFromCache(Long id);
    
    /**
     * 将菜品保存到缓存
     */
    void cacheDish(Dish dish);
    
    /**
     * 将菜品列表保存到缓存
     */
    void cacheDishes(List<Dish> dishes);
    
    /**
     * 从缓存中删除菜品
     */
    void removeDishFromCache(Long id);
    
    /**
     * 更新菜品缓存
     */
    void updateDishCache(Dish dish);
    
    /**
     * 清除所有菜品缓存
     */
    void clearAllDishCache();
    
    /**
     * 清除分类下的所有菜品缓存
     */
    void clearCategoryDishCache(Long categoryId);

    /**
     * 预热菜品缓存
     */
    void preloadDishCache();
} 