package com.restaurant.common.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.common.entity.Table;

import java.util.List;

/**
 * 桌台服务接口
 */
public interface TableService extends IService<Table> {

    /**
     * 根据桌台号查找桌台
     */
    Table findByTableNumber(String tableNumber);

    /**
     * 获取所有可用桌台
     */
    List<Table> getAvailableTables();

    /**
     * 更新桌台状态
     */
    boolean updateTableStatus(Long tableId, String status);

    /**
     * 生成桌台二维码
     */
    String generateQrCode(Long tableId);

    /**
     * 验证桌台二维码
     */
    Table validateQrCode(String qrCode);
}