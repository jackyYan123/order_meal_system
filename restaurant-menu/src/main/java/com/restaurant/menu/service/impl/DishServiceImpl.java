package com.restaurant.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import com.restaurant.menu.dto.CreateDishRequest;
import com.restaurant.menu.dto.DishDto;
import com.restaurant.menu.dto.UpdateDishRequest;
import com.restaurant.menu.dto.UpdateStockRequest;
import com.restaurant.menu.entity.Category;
import com.restaurant.menu.entity.Dish;
import com.restaurant.menu.mapper.CategoryMapper;
import com.restaurant.menu.mapper.DishMapper;
import com.restaurant.menu.service.DishCacheService;
import com.restaurant.menu.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 菜品服务实现
 */
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private CategoryMapper categoryMapper;
    
    @Autowired
    private DishCacheService dishCacheService;

    // 图片上传路径配置
    private static final String UPLOAD_DIR = "uploads/dishes/";
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif"};
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    @Override
    public List<DishDto> getAllDishes() {
        // 优先从缓存获取
        List<DishDto> cachedDishes = dishCacheService.getAllDishesFromCache();
        if (cachedDishes != null && !cachedDishes.isEmpty()) {
            return cachedDishes;
        }
        
        // 缓存未命中，从数据库获取
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtos = dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        // 将数据放入缓存
        dishCacheService.cacheDishes(dishes);
        
        return dishDtos;
    }

    @Override
    public List<DishDto> getAvailableDishes() {
        // 优先从缓存获取
        List<DishDto> cachedDishes = dishCacheService.getAvailableDishesFromCache();
        if (cachedDishes != null && !cachedDishes.isEmpty()) {
            return cachedDishes;
        }
        
        // 缓存未命中，从数据库获取
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getIsAvailable, true)
                   .gt(Dish::getStock, 0)
                   .orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtos = dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return dishDtos;
    }

    @Override
    public List<DishDto> getDishesByCategory(Long categoryId) {
        // 优先从缓存获取
        List<DishDto> cachedDishes = dishCacheService.getDishesByCategoryFromCache(categoryId);
        if (cachedDishes != null && !cachedDishes.isEmpty()) {
            return cachedDishes;
        }
        
        // 缓存未命中，从数据库获取
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId)
                   .orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtos = dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return dishDtos;
    }

    @Override
    public List<DishDto> getAvailableDishesByCategory(Long categoryId) {
        // 优先从缓存获取
        List<DishDto> cachedDishes = dishCacheService.getAvailableDishesByCategoryFromCache(categoryId);
        if (cachedDishes != null && !cachedDishes.isEmpty()) {
            return cachedDishes;
        }
        
        // 缓存未命中，从数据库获取
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId, categoryId)
                   .eq(Dish::getIsAvailable, true)
                   .gt(Dish::getStock, 0)
                   .orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        List<DishDto> dishDtos = dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        
        return dishDtos;
    }

    @Override
    public DishDto getDishById(Long id) {
        // 优先从缓存获取
        DishDto cachedDish = dishCacheService.getDishByIdFromCache(id);
        if (cachedDish != null) {
            return cachedDish;
        }
        
        // 缓存未命中，从数据库获取
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }
        
        // 将数据放入缓存
        dishCacheService.cacheDish(dish);
        
        return convertToDto(dish);
    }

    @Override
    @Transactional
    public DishDto createDish(CreateDishRequest request) {
        // 验证分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }

        // 检查菜品名称是否已存在
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, request.getName());
        Dish existingDish = dishMapper.selectOne(queryWrapper);
        if (existingDish != null) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "菜品名称已存在");
        }

        // 检查排序顺序是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getSortOrder, request.getSortOrder());
        Dish existingSortOrder = dishMapper.selectOne(queryWrapper);
        if (existingSortOrder != null) {
            // 自动调整排序顺序
            adjustSortOrderForInsert(request.getSortOrder());
        }

        Dish dish = new Dish();
        BeanUtils.copyProperties(request, dish);
        
        dishMapper.insert(dish);
        
        // 将新菜品加入缓存
        dishCacheService.cacheDish(dish);
        // 清除分类缓存
        dishCacheService.clearCategoryDishCache(dish.getCategoryId());
        
        return convertToDto(dish);
    }

    @Override
    @Transactional
    public DishDto updateDish(Long id, UpdateDishRequest request) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        // 验证分类是否存在
        Category category = categoryMapper.selectById(request.getCategoryId());
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }

        // 检查菜品名称是否已存在（排除当前菜品）
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getName, request.getName())
                   .ne(Dish::getId, id);
        Dish existingDish = dishMapper.selectOne(queryWrapper);
        if (existingDish != null) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "菜品名称已存在");
        }

        // 记录旧分类ID
        Long oldCategoryId = dish.getCategoryId();
        
        // 如果排序顺序发生变化，需要调整其他菜品的排序
        if (!dish.getSortOrder().equals(request.getSortOrder())) {
            adjustSortOrderForUpdate(id, dish.getSortOrder(), request.getSortOrder());
        }

        BeanUtils.copyProperties(request, dish);
        dishMapper.updateById(dish);
        
        // 更新缓存
        dishCacheService.updateDishCache(dish);
        
        // 如果分类发生变化，需要清除两个分类的缓存
        if (!oldCategoryId.equals(dish.getCategoryId())) {
            dishCacheService.clearCategoryDishCache(oldCategoryId);
            dishCacheService.clearCategoryDishCache(dish.getCategoryId());
        } else {
            dishCacheService.clearCategoryDishCache(dish.getCategoryId());
        }
        
        return convertToDto(dish);
    }

    @Override
    @Transactional
    public void deleteDish(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        // TODO: 检查是否有订单项关联此菜品，如有则不允许删除
        // 这里暂时跳过，等订单模块实现后再添加

        // 记录分类ID，用于删除后清除缓存
        Long categoryId = dish.getCategoryId();
        
        dishMapper.deleteById(id);
        
        // 调整其他菜品的排序顺序
        adjustSortOrderAfterDelete(dish.getSortOrder());
        
        // 从缓存中删除菜品
        dishCacheService.removeDishFromCache(id);
        // 清除分类缓存
        dishCacheService.clearCategoryDishCache(categoryId);
    }

    @Override
    @Transactional
    public void toggleDishStatus(Long id, Boolean isAvailable) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, id)
                    .set(Dish::getIsAvailable, isAvailable);
        
        dishMapper.update(null, updateWrapper);
        
        // 更新菜品信息
        dish.setIsAvailable(isAvailable);
        
        // 更新缓存
        dishCacheService.updateDishCache(dish);
        // 清除分类缓存，因为可用状态变化会影响可用菜品列表
        dishCacheService.clearCategoryDishCache(dish.getCategoryId());
    }

    @Override
    @Transactional
    public void updateStock(Long id, UpdateStockRequest request) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, id)
                    .set(Dish::getStock, request.getStock());
        
        dishMapper.update(null, updateWrapper);
        
        // 更新菜品信息
        dish.setStock(request.getStock());
        
        // 更新缓存
        dishCacheService.updateDishCache(dish);
        // 清除分类缓存，因为库存变化会影响可用菜品列表
        dishCacheService.clearCategoryDishCache(dish.getCategoryId());
    }

    @Override
    @Transactional
    public void batchUpdateStock(List<Long> dishIds, Integer stock) {
        if (dishIds == null || dishIds.isEmpty()) {
            return;
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(Dish::getId, dishIds)
                    .set(Dish::getStock, stock);
        
        dishMapper.update(null, updateWrapper);
        
        // 批量更新缓存
        for (Long dishId : dishIds) {
            Dish dish = dishMapper.selectById(dishId);
            if (dish != null) {
                dish.setStock(stock);
                // 更新缓存
                dishCacheService.updateDishCache(dish);
                // 清除分类缓存
                dishCacheService.clearCategoryDishCache(dish.getCategoryId());
            }
        }
        
        // 由于可能涉及到多个分类的菜品，清除所有菜品列表缓存
        dishCacheService.clearAllDishCache();
    }

    @Override
    @Transactional
    public void updateDishSort(Long id, Integer newSortOrder) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        Integer oldSortOrder = dish.getSortOrder();
        if (oldSortOrder.equals(newSortOrder)) {
            return; // 排序顺序没有变化
        }

        adjustSortOrderForUpdate(id, oldSortOrder, newSortOrder);
        
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, id)
                    .set(Dish::getSortOrder, newSortOrder);
        
        dishMapper.update(null, updateWrapper);
        
        // 更新菜品信息
        dish.setSortOrder(newSortOrder);
        
        // 更新缓存
        dishCacheService.updateDishCache(dish);
        // 清除分类缓存
        dishCacheService.clearCategoryDishCache(dish.getCategoryId());
        
        // 排序变化可能影响所有列表，清除所有菜品列表缓存
        dishCacheService.clearAllDishCache();
    }

    @Override
    public String uploadDishImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "文件大小不能超过5MB");
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "文件名不能为空");
        }

        String extension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        boolean isValidExtension = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(extension)) {
                isValidExtension = true;
                break;
            }
        }
        if (!isValidExtension) {
            throw new BusinessException(ErrorCode.INVALID_PARAMETER, "不支持的文件格式");
        }

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成唯一文件名
            String filename = UUID.randomUUID().toString() + extension;
            Path filePath = uploadPath.resolve(filename);

            // 保存文件
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 返回文件访问URL
            return "/uploads/dishes/" + filename;
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "文件上传失败");
        }
    }

    @Override
    public List<DishDto> searchDishes(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDishes();
        }

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(Dish::getName, keyword)
                   .or()
                   .like(Dish::getDescription, keyword)
                   .orderByAsc(Dish::getSortOrder);
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public boolean checkStock(Long dishId, Integer quantity) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            return false;
        }
        return dish.getStock() >= quantity;
    }

    @Override
    @Transactional
    public void reduceStock(Long dishId, Integer quantity) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        if (dish.getStock() < quantity) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "库存不足");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, dishId)
                    .setSql("stock = stock - " + quantity);
        
        dishMapper.update(null, updateWrapper);
        
        // 更新菜品信息
        dish.setStock(dish.getStock() - quantity);
        
        // 更新缓存
        dishCacheService.updateDishCache(dish);
        // 清除分类缓存
        dishCacheService.clearCategoryDishCache(dish.getCategoryId());
    }

    @Override
    @Transactional
    public void increaseStock(Long dishId, Integer quantity) {
        Dish dish = dishMapper.selectById(dishId);
        if (dish == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "菜品不存在");
        }

        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Dish::getId, dishId)
                    .setSql("stock = stock + " + quantity);
        
        dishMapper.update(null, updateWrapper);
        
        // 更新菜品信息
        dish.setStock(dish.getStock() + quantity);
        
        // 更新缓存
        dishCacheService.updateDishCache(dish);
        // 清除分类缓存
        dishCacheService.clearCategoryDishCache(dish.getCategoryId());
    }

    /**
     * 插入新菜品时调整排序顺序
     */
    private void adjustSortOrderForInsert(Integer insertSortOrder) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.ge(Dish::getSortOrder, insertSortOrder)
                    .setSql("sort_order = sort_order + 1");
        
        dishMapper.update(null, updateWrapper);
    }

    /**
     * 更新菜品时调整排序顺序
     */
    private void adjustSortOrderForUpdate(Long dishId, Integer oldSortOrder, Integer newSortOrder) {
        if (newSortOrder < oldSortOrder) {
            // 向前移动，需要将中间的菜品向后移动
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.ge(Dish::getSortOrder, newSortOrder)
                        .lt(Dish::getSortOrder, oldSortOrder)
                        .ne(Dish::getId, dishId)
                        .setSql("sort_order = sort_order + 1");
            
            dishMapper.update(null, updateWrapper);
        } else {
            // 向后移动，需要将中间的菜品向前移动
            LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.gt(Dish::getSortOrder, oldSortOrder)
                        .le(Dish::getSortOrder, newSortOrder)
                        .ne(Dish::getId, dishId)
                        .setSql("sort_order = sort_order - 1");
            
            dishMapper.update(null, updateWrapper);
        }
    }

    /**
     * 删除菜品后调整排序顺序
     */
    private void adjustSortOrderAfterDelete(Integer deletedSortOrder) {
        LambdaUpdateWrapper<Dish> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.gt(Dish::getSortOrder, deletedSortOrder)
                    .setSql("sort_order = sort_order - 1");
        
        dishMapper.update(null, updateWrapper);
    }

    @Override
    public List<DishDto> getRecommendedDishes() {
        // 获取推荐菜品：按销量和评分排序，取前10个可用菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getIsAvailable, true)
                   .gt(Dish::getStock, 0)
                   .orderByAsc(Dish::getSortOrder)
                   .last("LIMIT 10");
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<DishDto> getPopularDishes() {
        // 获取热门菜品：按排序顺序取前8个可用菜品
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getIsAvailable, true)
                   .gt(Dish::getStock, 0)
                   .orderByAsc(Dish::getSortOrder)
                   .last("LIMIT 8");
        
        List<Dish> dishes = dishMapper.selectList(queryWrapper);
        return dishes.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 实体转DTO
     */
    private DishDto convertToDto(Dish dish) {
        DishDto dto = new DishDto();
        BeanUtils.copyProperties(dish, dto);
        
        // 获取分类名称
        Category category = categoryMapper.selectById(dish.getCategoryId());
        if (category != null) {
            dto.setCategoryName(category.getName());
        }
        
        return dto;
    }
}