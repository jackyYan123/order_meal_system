package com.restaurant.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.restaurant.menu.dto.DishDto;
import com.restaurant.menu.entity.Category;
import com.restaurant.menu.entity.Dish;
import com.restaurant.menu.mapper.CategoryMapper;
import com.restaurant.menu.mapper.DishMapper;
import com.restaurant.menu.service.DishCacheService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品缓存服务实现类
 */
@Service
public class DishCacheServiceImpl implements DishCacheService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    // 缓存Key前缀
    private static final String CACHE_DISH_KEY = "dish:";
    private static final String CACHE_DISH_LIST_KEY = "dish:list:";
    private static final String CACHE_DISH_CATEGORY_KEY = "dish:category:";
    private static final String CACHE_DISH_AVAILABLE_KEY = "dish:available:";
    private static final String CACHE_DISH_CATEGORY_AVAILABLE_KEY = "dish:category:available:";

    // 缓存过期时间（分钟）
    private static final long CACHE_EXPIRE_TIME = 60;

    @Override
    public List<DishDto> getAllDishesFromCache() {
        String key = CACHE_DISH_LIST_KEY + "all";
        
        @SuppressWarnings("unchecked")
        List<DishDto> cachedDishes = (List<DishDto>) redisTemplate.opsForValue().get(key);
        
        if (cachedDishes != null) {
            return cachedDishes;
        }
        
        // 从数据库获取所有菜品
        List<Dish> dishes = dishMapper.selectList(null);
        List<DishDto> dishDtos = convertToDtoList(dishes);
        
        // 缓存菜品列表
        redisTemplate.opsForValue().set(key, dishDtos, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return dishDtos;
    }

    @Override
    public List<DishDto> getAvailableDishesFromCache() {
        String key = CACHE_DISH_AVAILABLE_KEY + "all";
        
        @SuppressWarnings("unchecked")
        List<DishDto> cachedDishes = (List<DishDto>) redisTemplate.opsForValue().get(key);
        
        if (cachedDishes != null) {
            return cachedDishes;
        }
        
        // 使用MyBatis-Plus的条件查询，避免查询所有数据后再过滤
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getIsAvailable, true)
                   .gt(Dish::getStock, 0)
                   .orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtos = convertToDtoList(dishes);
        
        // 缓存菜品列表
        redisTemplate.opsForValue().set(key, dishDtos, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return dishDtos;
    }

    @Override
    public List<DishDto> getDishesByCategoryFromCache(Long categoryId) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        
        String key = CACHE_DISH_CATEGORY_KEY + categoryId;
        
        @SuppressWarnings("unchecked")
        List<DishDto> cachedDishes = (List<DishDto>) redisTemplate.opsForValue().get(key);
        
        if (cachedDishes != null) {
            return cachedDishes;
        }
        
        // 使用MyBatis-Plus的条件查询，避免查询所有数据后再过滤
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId)
                   .orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtos = convertToDtoList(dishes);
        
        // 缓存菜品列表
        redisTemplate.opsForValue().set(key, dishDtos, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return dishDtos;
    }

    @Override
    public List<DishDto> getAvailableDishesByCategoryFromCache(Long categoryId) {
        if (categoryId == null) {
            return new ArrayList<>();
        }
        
        String key = CACHE_DISH_CATEGORY_AVAILABLE_KEY + categoryId;
        
        @SuppressWarnings("unchecked")
        List<DishDto> cachedDishes = (List<DishDto>) redisTemplate.opsForValue().get(key);
        
        if (cachedDishes != null) {
            return cachedDishes;
        }
        
        // 从数据库获取分类下的可用菜品
        List<Dish> dishes = dishMapper.selectList(null);
        dishes = dishes.stream()
                .filter(dish -> dish.getCategoryId().equals(categoryId) 
                        && dish.getIsAvailable()
                        && dish.getStock() > 0)
                .sorted((d1, d2) -> d1.getSortOrder().compareTo(d2.getSortOrder()))
                .collect(Collectors.toList());
        
        List<DishDto> dishDtos = convertToDtoList(dishes);
        
        // 缓存菜品列表
        redisTemplate.opsForValue().set(key, dishDtos, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return dishDtos;
    }

    @Override
    public DishDto getDishByIdFromCache(Long id) {
        if (id == null) {
            return null;
        }
        
        String key = CACHE_DISH_KEY + id;
        
        DishDto cachedDish = (DishDto) redisTemplate.opsForValue().get(key);
        
        if (cachedDish != null) {
            return cachedDish;
        }
        
        // 从数据库获取菜品
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            return null;
        }
        
        DishDto dishDto = convertToDto(dish);
        
        // 缓存菜品
        redisTemplate.opsForValue().set(key, dishDto, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        return dishDto;
    }

    @Override
    public void cacheDish(Dish dish) {
        if (dish == null || dish.getId() == null) {
            return;
        }
        
        String key = CACHE_DISH_KEY + dish.getId();
        DishDto dishDto = convertToDto(dish);
        
        // 缓存单个菜品
        redisTemplate.opsForValue().set(key, dishDto, CACHE_EXPIRE_TIME, TimeUnit.MINUTES);
        
        // 由于单个菜品变化会影响相关的菜品列表，需要清除相关列表缓存
        clearRelatedListCache(dish);
    }

    @Override
    public void cacheDishes(List<Dish> dishes) {
        if (dishes == null || dishes.isEmpty()) {
            return;
        }
        
        for (Dish dish : dishes) {
            cacheDish(dish);
        }
    }

    @Override
    public void removeDishFromCache(Long id) {
        if (id == null) {
            return;
        }
        
        // 获取菜品信息，用于后续清除相关缓存
        Dish dish = dishMapper.selectById(id);
        if (dish != null) {
            clearRelatedListCache(dish);
        }
        
        // 删除单个菜品缓存
        String key = CACHE_DISH_KEY + id;
        redisTemplate.delete(key);
    }

    @Override
    public void updateDishCache(Dish dish) {
        if (dish == null || dish.getId() == null) {
            return;
        }
        
        // 更新单个菜品缓存
        cacheDish(dish);
    }

    @Override
    public void clearAllDishCache() {
        // 使用Set来存储需要删除的key，避免使用keys命令
        Set<String> keysToDelete = new HashSet<>();
        
        // 清除所有菜品列表缓存
        keysToDelete.add(CACHE_DISH_LIST_KEY + "all");
        keysToDelete.add(CACHE_DISH_AVAILABLE_KEY + "all");
        
        // 清除所有分类的菜品缓存
        List<Category> categories = categoryMapper.selectList(null);
        for (Category category : categories) {
            keysToDelete.add(CACHE_DISH_CATEGORY_KEY + category.getId());
            keysToDelete.add(CACHE_DISH_CATEGORY_AVAILABLE_KEY + category.getId());
        }
        
        // 批量删除
        if (!keysToDelete.isEmpty()) {
            redisTemplate.delete(keysToDelete);
        }
        
        // 对于单个菜品缓存，我们通过维护一个菜品ID集合来管理
        clearAllSingleDishCache();
    }

    @Override
    public void clearCategoryDishCache(Long categoryId) {
        if (categoryId == null) {
            return;
        }
        
        // 清除特定分类的菜品缓存
        String categoryKey = CACHE_DISH_CATEGORY_KEY + categoryId;
        String categoryAvailableKey = CACHE_DISH_CATEGORY_AVAILABLE_KEY + categoryId;
        
        redisTemplate.delete(categoryKey);
        redisTemplate.delete(categoryAvailableKey);
        
        // 同时清除所有菜品列表缓存，因为分类的变化会影响总列表
        redisTemplate.delete(CACHE_DISH_LIST_KEY + "all");
        redisTemplate.delete(CACHE_DISH_AVAILABLE_KEY + "all");
    }

    @Override
    public void preloadDishCache() {
        // 预热缓存，将常用数据提前加载到缓存中
        getAllDishesFromCache();
        getAvailableDishesFromCache();
        
        // 预热所有分类的菜品缓存
        List<Category> categories = categoryMapper.selectList(null);
        for (Category category : categories) {
            getDishesByCategoryFromCache(category.getId());
            getAvailableDishesByCategoryFromCache(category.getId());
        }
    }
    
    /**
     * 清除与当前菜品相关的列表缓存
     */
    private void clearRelatedListCache(Dish dish) {
        if (dish == null) {
            return;
        }
        
        // 清除所有菜品列表缓存
        redisTemplate.delete(CACHE_DISH_LIST_KEY + "all");
        redisTemplate.delete(CACHE_DISH_AVAILABLE_KEY + "all");
        
        // 清除菜品所属分类的缓存
        if (dish.getCategoryId() != null) {
            String categoryKey = CACHE_DISH_CATEGORY_KEY + dish.getCategoryId();
            String categoryAvailableKey = CACHE_DISH_CATEGORY_AVAILABLE_KEY + dish.getCategoryId();
            redisTemplate.delete(categoryKey);
            redisTemplate.delete(categoryAvailableKey);
        }
    }
    
    /**
     * 转换菜品实体为DTO
     */
    private DishDto convertToDto(Dish dish) {
        if (dish == null) {
            return null;
        }
        
        DishDto dto = new DishDto();
        BeanUtils.copyProperties(dish, dto);
        
        // 获取分类名称
        if (dish.getCategoryId() != null) {
            Category category = categoryMapper.selectById(dish.getCategoryId());
            if (category != null) {
                dto.setCategoryName(category.getName());
            }
        }
        
        return dto;
    }
    
    /**
     * 批量转换菜品实体为DTO
     */
    private List<DishDto> convertToDtoList(List<Dish> dishes) {
        if (dishes == null || dishes.isEmpty()) {
            return new ArrayList<>();
        }
        
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    /**
     * 清除所有单个菜品缓存
     */
    private void clearAllSingleDishCache() {
        // 获取所有菜品ID，然后逐个删除缓存
        List<Dish> dishes = dishMapper.selectList(null);
        Set<String> dishKeys = new HashSet<>();
        
        for (Dish dish : dishes) {
            dishKeys.add(CACHE_DISH_KEY + dish.getId());
        }
        
        if (!dishKeys.isEmpty()) {
            redisTemplate.delete(dishKeys);
        }
    }
} 