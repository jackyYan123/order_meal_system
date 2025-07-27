package com.restaurant.menu.service;

import com.restaurant.menu.dto.CreateDishRequest;
import com.restaurant.menu.dto.DishDto;
import com.restaurant.menu.dto.UpdateDishRequest;
import com.restaurant.menu.dto.UpdateStockRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 菜品服务接口
 */
public interface DishService {

    /**
     * 获取所有菜品列表（按排序顺序）
     */
    List<DishDto> getAllDishes();

    /**
     * 获取可用的菜品列表（按排序顺序）
     */
    List<DishDto> getAvailableDishes();

    /**
     * 根据分类ID获取菜品列表
     */
    List<DishDto> getDishesByCategory(Long categoryId);

    /**
     * 根据分类ID获取可用的菜品列表
     */
    List<DishDto> getAvailableDishesByCategory(Long categoryId);

    /**
     * 根据ID获取菜品
     */
    DishDto getDishById(Long id);

    /**
     * 创建菜品
     */
    DishDto createDish(CreateDishRequest request);

    /**
     * 更新菜品
     */
    DishDto updateDish(Long id, UpdateDishRequest request);

    /**
     * 删除菜品
     */
    void deleteDish(Long id);

    /**
     * 启用/禁用菜品
     */
    void toggleDishStatus(Long id, Boolean isAvailable);

    /**
     * 更新菜品库存
     */
    void updateStock(Long id, UpdateStockRequest request);

    /**
     * 批量更新库存
     */
    void batchUpdateStock(List<Long> dishIds, Integer stock);

    /**
     * 调整菜品排序
     */
    void updateDishSort(Long id, Integer newSortOrder);

    /**
     * 上传菜品图片
     */
    String uploadDishImage(MultipartFile file);

    /**
     * 搜索菜品
     */
    List<DishDto> searchDishes(String keyword);

    /**
     * 检查菜品库存是否充足
     */
    boolean checkStock(Long dishId, Integer quantity);

    /**
     * 减少库存（下单时调用）
     */
    void reduceStock(Long dishId, Integer quantity);

    /**
     * 增加库存（取消订单时调用）
     */
    void increaseStock(Long dishId, Integer quantity);
    
    /**
     * 恢复库存（取消订单时调用，与increaseStock功能相同）
     */
    void restoreStock(Long dishId, Integer quantity);

    /**
     * 获取推荐菜品
     */
    List<DishDto> getRecommendedDishes();

    /**
     * 获取热门菜品
     */
    List<DishDto> getPopularDishes();
}