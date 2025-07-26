package com.restaurant.menu.service;

import com.restaurant.menu.dto.CategoryDto;
import com.restaurant.menu.dto.CreateCategoryRequest;
import com.restaurant.menu.dto.UpdateCategoryRequest;

import java.util.List;

/**
 * 菜品分类服务接口
 */
public interface CategoryService {

    /**
     * 获取所有分类列表（按排序顺序）
     */
    List<CategoryDto> getAllCategories();

    /**
     * 获取启用的分类列表（按排序顺序）
     */
    List<CategoryDto> getActiveCategories();

    /**
     * 根据ID获取分类
     */
    CategoryDto getCategoryById(Long id);

    /**
     * 创建分类
     */
    CategoryDto createCategory(CreateCategoryRequest request);

    /**
     * 更新分类
     */
    CategoryDto updateCategory(Long id, UpdateCategoryRequest request);

    /**
     * 删除分类
     */
    void deleteCategory(Long id);

    /**
     * 启用/禁用分类
     */
    void toggleCategoryStatus(Long id, Boolean isActive);

    /**
     * 调整分类排序
     */
    void updateCategorySort(Long id, Integer newSortOrder);
}