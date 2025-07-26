package com.restaurant.controller;

import com.restaurant.common.entity.Table;
import com.restaurant.common.result.Result;
import com.restaurant.common.service.TableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 桌台控制器
 */
@RestController
@RequestMapping("/api/tables")
@CrossOrigin(origins = "*")
public class TableController {

    @Autowired
    private TableService tableService;

    /**
     * 获取所有桌台
     */
    @GetMapping
    public Result<List<Table>> getAllTables() {
        try {
            List<Table> tables = tableService.list();
            return Result.success(tables);
        } catch (Exception e) {
            return Result.error("GET_TABLES_FAILED", "获取桌台列表失败：" + e.getMessage());
        }
    }

    /**
     * 获取可用桌台
     */
    @GetMapping("/available")
    public Result<List<Table>> getAvailableTables() {
        try {
            List<Table> tables = tableService.getAvailableTables();
            return Result.success(tables);
        } catch (Exception e) {
            return Result.error("GET_AVAILABLE_TABLES_FAILED", "获取可用桌台失败：" + e.getMessage());
        }
    }

    /**
     * 根据ID获取桌台信息
     */
    @GetMapping("/{id}")
    public Result<Table> getTableById(@PathVariable Long id) {
        try {
            Table table = tableService.getById(id);
            if (table == null) {
                return Result.error("TABLE_NOT_FOUND", "桌台不存在");
            }
            return Result.success(table);
        } catch (Exception e) {
            return Result.error("GET_TABLE_FAILED", "获取桌台信息失败：" + e.getMessage());
        }
    }

    /**
     * 根据桌台号获取桌台信息
     */
    @GetMapping("/number/{tableNumber}")
    public Result<Table> getTableByNumber(@PathVariable String tableNumber) {
        try {
            Table table = tableService.findByTableNumber(tableNumber);
            if (table == null) {
                return Result.error("TABLE_NOT_FOUND", "桌台不存在");
            }
            return Result.success(table);
        } catch (Exception e) {
            return Result.error("GET_TABLE_FAILED", "获取桌台信息失败：" + e.getMessage());
        }
    }

    /**
     * 更新桌台状态
     */
    @PutMapping("/{id}/status")
    public Result<Void> updateTableStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        try {
            String status = request.get("status");
            if (status == null || status.trim().isEmpty()) {
                return Result.error("PARAM_ERROR", "状态参数不能为空");
            }
            
            boolean success = tableService.updateTableStatus(id, status);
            if (success) {
                return Result.success();
            } else {
                return Result.error("UPDATE_FAILED", "更新桌台状态失败");
            }
        } catch (Exception e) {
            return Result.error("UPDATE_TABLE_STATUS_FAILED", "更新桌台状态失败：" + e.getMessage());
        }
    }

    /**
     * 生成桌台二维码
     */
    @PostMapping("/{id}/qrcode")
    public Result<String> generateQrCode(@PathVariable Long id) {
        try {
            String qrCode = tableService.generateQrCode(id);
            if (qrCode == null) {
                return Result.error("TABLE_NOT_FOUND", "桌台不存在");
            }
            return Result.success(qrCode);
        } catch (Exception e) {
            return Result.error("GENERATE_QRCODE_FAILED", "生成二维码失败：" + e.getMessage());
        }
    }

    /**
     * 验证桌台二维码
     */
    @PostMapping("/validate-qrcode")
    public Result<Table> validateQrCode(@RequestBody Map<String, String> request) {
        try {
            String qrCode = request.get("qrCode");
            if (qrCode == null || qrCode.trim().isEmpty()) {
                return Result.error("PARAM_ERROR", "二维码内容不能为空");
            }
            
            Table table = tableService.validateQrCode(qrCode);
            if (table == null) {
                return Result.error("INVALID_QRCODE", "无效的桌台二维码");
            }
            
            return Result.success(table);
        } catch (Exception e) {
            return Result.error("VALIDATE_QRCODE_FAILED", "验证二维码失败：" + e.getMessage());
        }
    }
}