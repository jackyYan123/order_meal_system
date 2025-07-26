package com.restaurant.notification.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.restaurant.notification.entity.Notification;
import com.restaurant.notification.service.WebSocketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * WebSocket服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebSocketServiceImpl implements WebSocketService {
    
    private final ObjectMapper objectMapper;
    
    // 存储用户会话信息
    private static final Map<Long, Session> USER_SESSIONS = new ConcurrentHashMap<>();
    // 存储角色会话信息
    private static final Map<String, CopyOnWriteArraySet<Session>> ROLE_SESSIONS = new ConcurrentHashMap<>();
    // 存储所有会话
    private static final CopyOnWriteArraySet<Session> ALL_SESSIONS = new CopyOnWriteArraySet<>();
    
    @Override
    public void sendNotification(Notification notification) {
        try {
            Map<String, Object> message = new HashMap<>();
            message.put("type", "NOTIFICATION");
            message.put("data", notification);
            
            String jsonMessage = objectMapper.writeValueAsString(message);
            
            // 根据接收者类型发送通知
            if (notification.getRecipientId() != null) {
                // 发送给指定用户
                sendMessageToUser(notification.getRecipientId(), jsonMessage);
            } else if ("ALL".equals(notification.getRecipientType())) {
                // 广播给所有用户
                broadcastMessage(jsonMessage);
            } else {
                // 发送给指定角色
                sendMessageToRole(notification.getRecipientType(), jsonMessage);
            }
            
            log.info("WebSocket通知发送成功: {}", notification.getId());
            
        } catch (Exception e) {
            log.error("WebSocket通知发送失败: {}", notification.getId(), e);
            throw new RuntimeException("WebSocket通知发送失败", e);
        }
    }
    
    @Override
    public void sendMessageToUser(Long userId, String message) {
        Session session = USER_SESSIONS.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
                log.debug("消息发送给用户 {}: {}", userId, message);
            } catch (Exception e) {
                log.error("发送消息给用户失败: {}", userId, e);
                // 移除无效会话
                USER_SESSIONS.remove(userId);
            }
        } else {
            log.warn("用户 {} 不在线或会话已关闭", userId);
        }
    }
    
    @Override
    public void sendMessageToRole(String role, String message) {
        CopyOnWriteArraySet<Session> sessions = ROLE_SESSIONS.get(role);
        if (sessions != null && !sessions.isEmpty()) {
            for (Session session : sessions) {
                if (session.isOpen()) {
                    try {
                        session.getBasicRemote().sendText(message);
                    } catch (Exception e) {
                        log.error("发送消息给角色 {} 失败", role, e);
                        sessions.remove(session);
                    }
                } else {
                    sessions.remove(session);
                }
            }
            log.debug("消息发送给角色 {}: {}", role, message);
        } else {
            log.warn("角色 {} 没有在线用户", role);
        }
    }
    
    @Override
    public void broadcastMessage(String message) {
        for (Session session : ALL_SESSIONS) {
            if (session.isOpen()) {
                try {
                    session.getBasicRemote().sendText(message);
                } catch (Exception e) {
                    log.error("广播消息失败", e);
                    ALL_SESSIONS.remove(session);
                }
            } else {
                ALL_SESSIONS.remove(session);
            }
        }
        log.debug("广播消息: {}", message);
    }
    
    @Override
    public int getOnlineUserCount() {
        return ALL_SESSIONS.size();
    }
    
    @Override
    public boolean isUserOnline(Long userId) {
        Session session = USER_SESSIONS.get(userId);
        return session != null && session.isOpen();
    }
    
    /**
     * 添加用户会话
     */
    public static void addUserSession(Long userId, String role, Session session) {
        USER_SESSIONS.put(userId, session);
        ALL_SESSIONS.add(session);
        
        // 添加到角色会话组
        ROLE_SESSIONS.computeIfAbsent(role, k -> new CopyOnWriteArraySet<>()).add(session);
        
        log.info("用户 {} (角色: {}) 连接WebSocket", userId, role);
    }
    
    /**
     * 移除用户会话
     */
    public static void removeUserSession(Long userId, String role, Session session) {
        USER_SESSIONS.remove(userId);
        ALL_SESSIONS.remove(session);
        
        // 从角色会话组移除
        CopyOnWriteArraySet<Session> roleSessions = ROLE_SESSIONS.get(role);
        if (roleSessions != null) {
            roleSessions.remove(session);
        }
        
        log.info("用户 {} (角色: {}) 断开WebSocket连接", userId, role);
    }
}