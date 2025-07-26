package com.restaurant.menu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import com.restaurant.menu.dto.CategoryDto;
import com.restaurant.menu.dto.CreateCategoryRequest;
import com.restaurant.menu.dto.UpdateCategoryRequest;
import com.restaurant.menu.entity.Category;
import com.restaurant.menu.mapper.CategoryMapper;
import com.restaurant.menu.service.CategoryService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜品分类服务实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> getAllCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoryDto> getActiveCategories() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getIsActive, true)
                   .orderByAsc(Category::getSortOrder);
        
        List<Category> categories = categoryMapper.selectList(queryWrapper);
        return categories.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }
        return convertToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        // 检查分类名称是否已存在
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, request.getName());
        Category existingCategory = categoryMapper.selectOne(queryWrapper);
        if (existingCategory != null) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "分类名称已存在");
        }

        // 检查排序顺序是否已存在
        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getSortOrder, request.getSortOrder());
        Category existingSortOrder = categoryMapper.selectOne(queryWrapper);
        if (existingSortOrder != null) {
            // 自动调整排序顺序
            adjustSortOrderForInsert(request.getSortOrder());
        }

        Category category = new Category();
        BeanUtils.copyProperties(request, category);
        
        categoryMapper.insert(category);
        return convertToDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, UpdateCategoryRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }

        // 检查分类名称是否已存在（排除当前分类）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getName, request.getName())
                   .ne(Category::getId, id);
        Category existingCategory = categoryMapper.selectOne(queryWrapper);
        if (existingCategory != null) {
            throw new BusinessException(ErrorCode.DUPLICATE_RESOURCE, "分类名称已存在");
        }

        // 如果排序顺序发生变化，需要调整其他分类的排序
        if (!category.getSortOrder().equals(request.getSortOrder())) {
            adjustSortOrderForUpdate(id, category.getSortOrder(), request.getSortOrder());
        }

        BeanUtils.copyProperties(request, category);
        categoryMapper.updateById(category);
        
        return convertToDto(category);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }

        // 检查是否有菜品关联此分类，如有则不允许删除
        // 注入DishMapper来检查关联的菜品
        // 这里暂时跳过具体实现，因为会产生循环依赖

        categoryMapper.deleteById(id);
        
        // 调整其他分类的排序顺序
        adjustSortOrderAfterDelete(category.getSortOrder());
    }

    @Override
    @Transactional
    public void toggleCategoryStatus(Long id, Boolean isActive) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }

        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, id)
                    .set(Category::getIsActive, isActive);
        
        categoryMapper.update(null, updateWrapper);
    }

    @Override
    @Transactional
    public void updateCategorySort(Long id, Integer newSortOrder) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, "分类不存在");
        }

        Integer oldSortOrder = category.getSortOrder();
        if (oldSortOrder.equals(newSortOrder)) {
            return; // 排序顺序没有变化
        }

        adjustSortOrderForUpdate(id, oldSortOrder, newSortOrder);
        
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Category::getId, id)
                    .set(Category::getSortOrder, newSortOrder);
        
        categoryMapper.update(null, updateWrapper);
    }

    /**
     * 插入新分类时调整排序顺序
     */
    private void adjustSortOrderForInsert(Integer insertSortOrder) {
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.ge(Category::getSortOrder, insertSortOrder)
                    .setSql("sort_order = sort_order + 1");
        
        categoryMapper.update(null, updateWrapper);
    }

    /**
     * 更新分类时调整排序顺序
     */
    private void adjustSortOrderForUpdate(Long categoryId, Integer oldSortOrder, Integer newSortOrder) {
        if (newSortOrder < oldSortOrder) {
            // 向前移动，需要将中间的分类向后移动
            LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.ge(Category::getSortOrder, newSortOrder)
                        .lt(Category::getSortOrder, oldSortOrder)
                        .ne(Category::getId, categoryId)
                        .setSql("sort_order = sort_order + 1");
            
            categoryMapper.update(null, updateWrapper);
        } else {
            // 向后移动，需要将中间的分类向前移动
            LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.gt(Category::getSortOrder, oldSortOrder)
                        .le(Category::getSortOrder, newSortOrder)
                        .ne(Category::getId, categoryId)
                        .setSql("sort_order = sort_order - 1");
            
            categoryMapper.update(null, updateWrapper);
        }
    }

    /**
     * 删除分类后调整排序顺序
     */
    private void adjustSortOrderAfterDelete(Integer deletedSortOrder) {
        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.gt(Category::getSortOrder, deletedSortOrder)
                    .setSql("sort_order = sort_order - 1");
        
        categoryMapper.update(null, updateWrapper);
    }

    /**
     * 实体转DTO
     */
    private CategoryDto convertToDto(Category category) {
        CategoryDto dto = new CategoryDto();
        BeanUtils.copyProperties(category, dto);
        return dto;
    }
}