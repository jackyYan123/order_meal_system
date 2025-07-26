package com.restaurant.menu.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 更新桌台状态请求
 */
@Data
public class UpdateTableStatusRequest {
    
    /**
     * 状态：AVAILABLE-空闲, OCCUPIED-占用, RESERVED-预订
     */
    @NotBlank(message = "状态不能为空")
    private String status;
    
    /**
     * 备注信息
     */
    private String remark;
}