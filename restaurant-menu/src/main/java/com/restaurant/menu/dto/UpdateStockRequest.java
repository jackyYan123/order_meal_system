package com.restaurant.menu.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 更新库存请求DTO
 */
public class UpdateStockRequest {

    @NotNull(message = "库存数量不能为空")
    @Min(value = 0, message = "库存数量不能小于0")
    private Integer stock;

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}