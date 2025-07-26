package com.restaurant.menu.controller;

import com.restaurant.common.result.Result;
import com.restaurant.menu.dto.CategoryDto;
import com.restaurant.menu.dto.CreateCategoryRequest;
import com.restaurant.menu.dto.UpdateCategoryRequest;
import com.restaurant.menu.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 菜品分类控制器
 */
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 获取所有分类列表
     */
    @GetMapping
    public Result<List<CategoryDto>> getAllCategories() {
        List<CategoryDto> categories = categoryService.getAllCategories();
        return Result.success(categories);
    }

    /**
     * 获取启用的分类列表
     */
    @GetMapping("/active")
    public Result<List<CategoryDto>> getActiveCategories() {
        List<CategoryDto> categories = categoryService.getActiveCategories();
        return Result.success(categories);
    }

    /**
     * 根据ID获取分类
     */
    @GetMapping("/{id}")
    public Result<CategoryDto> getCategoryById(@PathVariable Long id) {
        CategoryDto category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

    /**
     * 创建分类
     */
    @PostMapping
    public Result<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryRequest request) {
        CategoryDto category = categoryService.createCategory(request);
        return Result.success("分类创建成功", category);
    }

    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    public Result<CategoryDto> updateCategory(@PathVariable Long id, 
                                            @Valid @RequestBody UpdateCategoryRequest request) {
        CategoryDto category = categoryService.updateCategory(id, request);
        return Result.success("分类更新成功", category);
    }

    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("分类删除成功");
    }

    /**
     * 启用/禁用分类
     */
    @PutMapping("/{id}/status")
    public Result<String> toggleCategoryStatus(@PathVariable Long id, 
                                           @RequestParam Boolean isActive) {
        categoryService.toggleCategoryStatus(id, isActive);
        String message = isActive ? "分类已启用" : "分类已禁用";
        return Result.success(message);
    }

    /**
     * 调整分类排序
     */
    @PutMapping("/{id}/sort")
    public Result<String> updateCategorySort(@PathVariable Long id, 
                                         @RequestParam Integer sortOrder) {
        categoryService.updateCategorySort(id, sortOrder);
        return Result.success("排序调整成功");
    }
}