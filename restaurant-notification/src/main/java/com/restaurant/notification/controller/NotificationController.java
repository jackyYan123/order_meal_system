package com.restaurant.notification.controller;

import com.restaurant.common.result.Result;
import com.restaurant.notification.dto.CreateNotificationRequest;
import com.restaurant.notification.entity.Notification;
import com.restaurant.notification.service.NotificationService;
import com.restaurant.notification.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 通知控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    private final WebSocketService webSocketService;
    
    /**
     * 创建通知
     */
    @PostMapping
    public Result<Notification> createNotification(@Valid @RequestBody CreateNotificationRequest request) {
        log.info("创建通知请求: {}", request);
        Notification notification = notificationService.createNotification(request);
        return Result.success(notification);
    }
    
    /**
     * 发送通知
     */
    @PostMapping("/{notificationId}/send")
    public Result<Void> sendNotification(@PathVariable Long notificationId) {
        notificationService.sendNotification(notificationId);
        return Result.success();
    }
    
    /**
     * 标记通知为已读
     */
    @PutMapping("/{notificationId}/read")
    public Result<Void> markAsRead(@PathVariable Long notificationId, @RequestParam Long userId) {
        notificationService.markAsRead(notificationId, userId);
        return Result.success();
    }
    
    /**
     * 获取用户未读通知
     */
    @GetMapping("/unread")
    public Result<List<Notification>> getUnreadNotifications(@RequestParam Long userId, 
                                                            @RequestParam String recipientType) {
        List<Notification> notifications = notificationService.getUnreadNotifications(userId, recipientType);
        return Result.success(notifications);
    }
    
    /**
     * 获取用户所有通知
     */
    @GetMapping("/user")
    public Result<List<Notification>> getUserNotifications(@RequestParam Long userId,
                                                          @RequestParam String recipientType,
                                                          @RequestParam(defaultValue = "1") int page,
                                                          @RequestParam(defaultValue = "20") int size) {
        List<Notification> notifications = notificationService.getUserNotifications(userId, recipientType, page, size);
        return Result.success(notifications);
    }
    
    /**
     * 发送订单通知
     */
    @PostMapping("/order")
    public Result<Void> sendOrderNotification(@RequestParam String type,
                                            @RequestParam Long orderId,
                                            @RequestParam(required = false) Long tableId,
                                            @RequestParam String content) {
        notificationService.sendOrderNotification(type, orderId, tableId, content);
        return Result.success();
    }
    
    /**
     * 发送支付通知
     */
    @PostMapping("/payment")
    public Result<Void> sendPaymentNotification(@RequestParam String type,
                                              @RequestParam Long orderId,
                                              @RequestParam String content) {
        notificationService.sendPaymentNotification(type, orderId, content);
        return Result.success();
    }
    
    /**
     * 发送系统通知
     */
    @PostMapping("/system")
    public Result<Void> sendSystemNotification(@RequestParam String title,
                                             @RequestParam String content,
                                             @RequestParam String recipientType) {
        notificationService.sendSystemNotification(title, content, recipientType);
        return Result.success();
    }
    
    /**
     * 获取在线用户数量
     */
    @GetMapping("/online-count")
    public Result<Integer> getOnlineUserCount() {
        int count = webSocketService.getOnlineUserCount();
        return Result.success(count);
    }
    
    /**
     * 检查用户是否在线
     */
    @GetMapping("/online/{userId}")
    public Result<Boolean> isUserOnline(@PathVariable Long userId) {
        boolean online = webSocketService.isUserOnline(userId);
        return Result.success(online);
    }
    
    /**
     * 广播消息（管理员功能）
     */
    @PostMapping("/broadcast")
    public Result<Void> broadcastMessage(@RequestParam String message) {
        webSocketService.broadcastMessage(message);
        return Result.success();
    }
}