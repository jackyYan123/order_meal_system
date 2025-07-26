package com.restaurant.common.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.common.entity.Table;
import com.restaurant.common.mapper.TableMapper;
import com.restaurant.common.service.TableService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 桌台服务实现类
 */
@Service
public class TableServiceImpl extends ServiceImpl<TableMapper, Table> implements TableService {

    @Override
    public Table findByTableNumber(String tableNumber) {
        if (tableNumber == null || tableNumber.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<Table> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("table_number", tableNumber);
        return this.getOne(queryWrapper);
    }

    @Override
    public List<Table> getAvailableTables() {
        QueryWrapper<Table> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", "AVAILABLE");
        return this.list(queryWrapper);
    }

    @Override
    public boolean updateTableStatus(Long tableId, String status) {
        if (tableId == null || status == null) {
            return false;
        }
        
        Table table = this.getById(tableId);
        if (table != null) {
            table.setStatus(status);
            return this.updateById(table);
        }
        return false;
    }

    @Override
    public String generateQrCode(Long tableId) {
        Table table = this.getById(tableId);
        if (table == null) {
            return null;
        }
        
        // 生成二维码JSON内容
        String qrContent = String.format(
            "{\"tableId\":%d,\"tableName\":\"%s\",\"capacity\":%d}",
            table.getId(),
            table.getTableNumber(),
            table.getCapacity()
        );
        
        // 更新数据库中的二维码内容
        table.setQrCode(qrContent);
        this.updateById(table);
        
        return qrContent;
    }

    @Override
    public Table validateQrCode(String qrCode) {
        if (qrCode == null || qrCode.trim().isEmpty()) {
            return null;
        }
        
        try {
            // 这里可以解析二维码内容并验证
            // 简单实现：直接通过二维码内容查找
            QueryWrapper<Table> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("qr_code", qrCode);
            return this.getOne(queryWrapper);
        } catch (Exception e) {
            return null;
        }
    }
}