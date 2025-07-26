package com.restaurant.notification.service;

import com.restaurant.notification.dto.CreateNotificationRequest;
import com.restaurant.notification.entity.Notification;

import java.util.List;

/**
 * 通知服务接口
 */
public interface NotificationService {
    
    /**
     * 创建通知
     */
    Notification createNotification(CreateNotificationRequest request);
    
    /**
     * 发送通知
     */
    void sendNotification(Long notificationId);
    
    /**
     * 批量发送通知
     */
    void sendNotifications(List<Long> notificationIds);
    
    /**
     * 标记通知为已读
     */
    void markAsRead(Long notificationId, Long userId);
    
    /**
     * 获取用户未读通知
     */
    List<Notification> getUnreadNotifications(Long userId, String recipientType);
    
    /**
     * 获取用户所有通知
     */
    List<Notification> getUserNotifications(Long userId, String recipientType, int page, int size);
    
    /**
     * 发送订单相关通知
     */
    void sendOrderNotification(String type, Long orderId, Long tableId, String content);
    
    /**
     * 发送支付相关通知
     */
    void sendPaymentNotification(String type, Long orderId, String content);
    
    /**
     * 发送系统通知
     */
    void sendSystemNotification(String title, String content, String recipientType);
}