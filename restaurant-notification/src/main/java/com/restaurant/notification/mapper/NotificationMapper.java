package com.restaurant.notification.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.notification.entity.Notification;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知Mapper
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
    
    /**
     * 查询用户未读通知数量
     */
    Long countUnreadNotifications(@Param("userId") Long userId, 
                                 @Param("recipientType") String recipientType);
    
    /**
     * 批量标记通知为已读
     */
    int markNotificationsAsRead(@Param("userId") Long userId,
                               @Param("recipientType") String recipientType,
                               @Param("notificationIds") List<Long> notificationIds);
    
    /**
     * 清理过期通知
     */
    int deleteExpiredNotifications(@Param("expireTime") LocalDateTime expireTime);
    
    /**
     * 查询待发送的通知
     */
    List<Notification> selectPendingNotifications();
}