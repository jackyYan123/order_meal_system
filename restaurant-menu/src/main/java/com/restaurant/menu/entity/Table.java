package com.restaurant.menu.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.restaurant.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 桌台实体类
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tables")
public class Table extends BaseEntity {
    
    /**
     * 桌台号
     */
    private String tableNumber;
    
    /**
     * 容纳人数
     */
    private Integer capacity;
    
    /**
     * 状态：AVAILABLE-空闲, OCCUPIED-占用, RESERVED-预订
     */
    private String status;
    
    /**
     * 二维码
     */
    private String qrCode;
}