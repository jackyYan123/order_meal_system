package com.restaurant.menu.dto;

import lombok.Data;

import javax.validation.constraints.Min;

/**
 * 更新桌台请求
 */
@Data
public class UpdateTableRequest {
    
    /**
     * 桌台号
     */
    private String tableNumber;
    
    /**
     * 容纳人数
     */
    @Min(value = 1, message = "容纳人数至少为1")
    private Integer capacity;
    
    /**
     * 二维码
     */
    private String qrCode;
}