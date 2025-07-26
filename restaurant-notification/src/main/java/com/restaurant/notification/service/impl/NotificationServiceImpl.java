package com.restaurant.notification.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.common.exception.BusinessException;
import com.restaurant.common.exception.ErrorCode;
import com.restaurant.notification.dto.CreateNotificationRequest;
import com.restaurant.notification.entity.Notification;
import com.restaurant.notification.enums.NotificationType;
import com.restaurant.notification.enums.RecipientType;
import com.restaurant.notification.mapper.NotificationMapper;
import com.restaurant.notification.service.NotificationService;
import com.restaurant.notification.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final NotificationMapper notificationMapper;
    private final WebSocketService webSocketService;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notification createNotification(CreateNotificationRequest request) {
        log.info("创建通知: {}", request);
        
        Notification notification = new Notification();
        notification.setType(request.getType());
        notification.setRecipientType(request.getRecipientType());
        notification.setRecipientId(request.getRecipientId());
        notification.setTitle(request.getTitle());
        notification.setContent(request.getContent());
        notification.setOrderId(request.getOrderId());
        notification.setTableId(request.getTableId());
        notification.setChannel(request.getChannel() != null ? request.getChannel() : "WEBSOCKET");
        notification.setStatus("PENDING");
        notification.setIsRead(false);
        
        notificationMapper.insert(notification);
        
        // 立即发送通知
        sendNotification(notification.getId());
        
        return notification;
    }
    
    @Override
    public void sendNotification(Long notificationId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
        
        if ("SENT".equals(notification.getStatus())) {
            log.warn("通知已发送，跳过重复发送: {}", notificationId);
            return;
        }
        
        try {
            // 根据通知渠道发送
            switch (notification.getChannel()) {
                case "WEBSOCKET":
                    sendWebSocketNotification(notification);
                    break;
                case "SMS":
                    sendSmsNotification(notification);
                    break;
                case "EMAIL":
                    sendEmailNotification(notification);
                    break;
                default:
                    sendWebSocketNotification(notification);
                    break;
            }
            
            // 更新发送状态
            notification.setStatus("SENT");
            notification.setSentTime(LocalDateTime.now());
            notificationMapper.updateById(notification);
            
            log.info("通知发送成功: {}", notificationId);
            
        } catch (Exception e) {
            log.error("通知发送失败: {}", notificationId, e);
            
            notification.setStatus("FAILED");
            notification.setFailureReason(e.getMessage());
            notificationMapper.updateById(notification);
        }
    }
    
    @Override
    public void sendNotifications(List<Long> notificationIds) {
        for (Long notificationId : notificationIds) {
            try {
                sendNotification(notificationId);
            } catch (Exception e) {
                log.error("批量发送通知失败: {}", notificationId, e);
            }
        }
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationMapper.selectById(notificationId);
        if (notification == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "通知不存在");
        }
        
        // 检查用户是否有权限标记此通知为已读
        if (notification.getRecipientId() != null && !notification.getRecipientId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "无权限操作此通知");
        }
        
        notification.setIsRead(true);
        notification.setReadTime(LocalDateTime.now());
        notificationMapper.updateById(notification);
        
        log.info("通知已标记为已读: {}, 用户: {}", notificationId, userId);
    }
    
    @Override
    public List<Notification> getUnreadNotifications(Long userId, String recipientType) {
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notification::getIsRead, false)
               .and(w -> w.eq(Notification::getRecipientType, recipientType)
                         .or()
                         .eq(Notification::getRecipientType, "ALL"))
               .and(w -> w.isNull(Notification::getRecipientId)
                         .or()
                         .eq(Notification::getRecipientId, userId))
               .orderByDesc(Notification::getCreatedAt);
        
        return notificationMapper.selectList(wrapper);
    }
    
    @Override
    public List<Notification> getUserNotifications(Long userId, String recipientType, int page, int size) {
        Page<Notification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Notification> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.eq(Notification::getRecipientType, recipientType)
                         .or()
                         .eq(Notification::getRecipientType, "ALL"))
               .and(w -> w.isNull(Notification::getRecipientId)
                         .or()
                         .eq(Notification::getRecipientId, userId))
               .orderByDesc(Notification::getCreatedAt);
        
        Page<Notification> result = notificationMapper.selectPage(pageParam, wrapper);
        return result.getRecords();
    }
    
    @Override
    public void sendOrderNotification(String type, Long orderId, Long tableId, String content) {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setType(type);
        request.setTitle(getNotificationTitle(type));
        request.setContent(content);
        request.setOrderId(orderId);
        request.setTableId(tableId);
        
        // 根据通知类型确定接收者
        switch (type) {
            case "ORDER_CREATED":
                request.setRecipientType("STAFF");
                break;
            case "ORDER_CONFIRMED":
            case "ORDER_PREPARING":
                request.setRecipientType("CHEF");
                break;
            case "ORDER_READY":
            case "ORDER_COMPLETED":
                request.setRecipientType("CUSTOMER");
                break;
            default:
                request.setRecipientType("ALL");
                break;
        }
        
        createNotification(request);
    }
    
    @Override
    public void sendPaymentNotification(String type, Long orderId, String content) {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setType(type);
        request.setTitle(getNotificationTitle(type));
        request.setContent(content);
        request.setOrderId(orderId);
        request.setRecipientType("CUSTOMER");
        
        createNotification(request);
    }
    
    @Override
    public void sendSystemNotification(String title, String content, String recipientType) {
        CreateNotificationRequest request = new CreateNotificationRequest();
        request.setType("SYSTEM_MAINTENANCE");
        request.setTitle(title);
        request.setContent(content);
        request.setRecipientType(recipientType);
        
        createNotification(request);
    }
    
    /**
     * 发送WebSocket通知
     */
    private void sendWebSocketNotification(Notification notification) {
        webSocketService.sendNotification(notification);
    }
    
    /**
     * 发送短信通知
     */
    private void sendSmsNotification(Notification notification) {
        // TODO: 集成短信服务
        log.info("发送短信通知: {}", notification.getContent());
    }
    
    /**
     * 发送邮件通知
     */
    private void sendEmailNotification(Notification notification) {
        // TODO: 集成邮件服务
        log.info("发送邮件通知: {}", notification.getContent());
    }
    
    /**
     * 获取通知标题
     */
    private String getNotificationTitle(String type) {
        try {
            NotificationType notificationType = NotificationType.fromCode(type);
            return notificationType.getDescription();
        } catch (Exception e) {
            return "系统通知";
        }
    }
}