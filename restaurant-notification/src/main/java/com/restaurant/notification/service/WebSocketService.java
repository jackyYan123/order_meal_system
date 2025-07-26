package com.restaurant.notification.service;

import com.restaurant.notification.entity.Notification;

/**
 * WebSocket服务接口
 */
public interface WebSocketService {
    
    /**
     * 发送通知到WebSocket
     */
    void sendNotification(Notification notification);
    
    /**
     * 发送消息给指定用户
     */
    void sendMessageToUser(Long userId, String message);
    
    /**
     * 发送消息给指定角色的所有用户
     */
    void sendMessageToRole(String role, String message);
    
    /**
     * 广播消息给所有在线用户
     */
    void broadcastMessage(String message);
    
    /**
     * 获取在线用户数量
     */
    int getOnlineUserCount();
    
    /**
     * 检查用户是否在线
     */
    boolean isUserOnline(Long userId);
}