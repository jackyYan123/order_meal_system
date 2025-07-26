package com.restaurant.notification.websocket;

import com.restaurant.notification.service.impl.WebSocketServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

/**
 * 通知WebSocket端点
 */
@Slf4j
@Component
@ServerEndpoint("/ws/notifications/{userId}/{role}")
public class NotificationWebSocket {
    
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") Long userId, @PathParam("role") String role) {
        try {
            WebSocketServiceImpl.addUserSession(userId, role, session);
            
            // 发送连接成功消息
            session.getBasicRemote().sendText("{\"type\":\"CONNECT\",\"message\":\"连接成功\"}");
            
            log.info("WebSocket连接建立成功，用户ID: {}, 角色: {}", userId, role);
            
        } catch (Exception e) {
            log.error("WebSocket连接建立失败", e);
        }
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("userId") Long userId, @PathParam("role") String role) {
        WebSocketServiceImpl.removeUserSession(userId, role, session);
        log.info("WebSocket连接关闭，用户ID: {}, 角色: {}", userId, role);
    }
    
    /**
     * 收到客户端消息后调用的方法
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("userId") Long userId) {
        log.debug("收到用户 {} 的消息: {}", userId, message);
        
        try {
            // 处理心跳消息
            if ("ping".equals(message)) {
                session.getBasicRemote().sendText("pong");
                return;
            }
            
            // 这里可以处理其他类型的消息
            // 比如标记通知为已读等
            
        } catch (Exception e) {
            log.error("处理WebSocket消息失败", e);
        }
    }
    
    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error, @PathParam("userId") Long userId, @PathParam("role") String role) {
        log.error("WebSocket发生错误，用户ID: {}, 角色: {}", userId, role, error);
        WebSocketServiceImpl.removeUserSession(userId, role, session);
    }
}