package com.restaurant.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.restaurant.common.entity.BaseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 菜品实体
 */
@TableName("dishes")
public class Dish extends BaseEntity {

    /**
     * 分类ID
     */
    private Long categoryId;

    /**
     * 菜品名称
     */
    private String name;

    /**
     * 菜品描述
     */
    private String description;

    /**
     * 价格
     */
    private BigDecimal price;

    /**
     * 图片URL
     */
    private String imageUrl;

    /**
     * 库存数量
     */
    private Integer stock;

    /**
     * 是否可用
     */
    private Boolean isAvailable;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    // Override setters from BaseEntity to make them accessible in tests
    @Override
    public void setId(Long id) {
        super.setId(id);
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        super.setCreatedAt(createdAt);
    }

    @Override
    public void setUpdatedAt(LocalDateTime updatedAt) {
        super.setUpdatedAt(updatedAt);
    }
}