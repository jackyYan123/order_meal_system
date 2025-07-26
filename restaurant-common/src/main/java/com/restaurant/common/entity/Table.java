package com.restaurant.common.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 桌台实体类
 */
@TableName("tables")
public class Table extends BaseEntity {

    /**
     * 桌台号
     */
    @TableField("table_number")
    private String tableNumber;

    /**
     * 容纳人数
     */
    @TableField("capacity")
    private Integer capacity;

    /**
     * 状态：AVAILABLE-可用, OCCUPIED-占用中, RESERVED-预订
     */
    @TableField("status")
    private String status;

    /**
     * 二维码内容
     */
    @TableField("qr_code")
    private String qrCode;

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrCode() {
        return qrCode;
    }

    public void setQrCode(String qrCode) {
        this.qrCode = qrCode;
    }
}