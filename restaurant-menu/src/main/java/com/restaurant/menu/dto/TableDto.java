package com.restaurant.menu.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 桌台DTO
 */
@Data
public class TableDto {
    
    private Long id;
    
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
     * 状态描述
     */
    private String statusDesc;
    
    /**
     * 二维码
     */
    private String qrCode;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime updatedAt;
}