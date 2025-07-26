package com.restaurant.menu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.menu.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * 菜品分类Mapper
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}