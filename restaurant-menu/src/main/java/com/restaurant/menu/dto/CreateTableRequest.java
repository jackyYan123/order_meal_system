package com.restaurant.menu.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 创建桌台请求
 */
@Data
public class CreateTableRequest {
    
    /**
     * 桌台号
     */
    @NotBlank(message = "桌台号不能为空")
    private String tableNumber;
    
    /**
     * 容纳人数
     */
    @NotNull(message = "容纳人数不能为空")
    @Min(value = 1, message = "容纳人数至少为1")
    private Integer capacity;
    
    /**
     * 二维码
     */
    private String qrCode;
}