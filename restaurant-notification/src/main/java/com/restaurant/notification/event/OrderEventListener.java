package com.restaurant.notification.event;

import com.restaurant.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 订单事件监听器
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventListener {
    
    private final NotificationService notificationService;
    
    /**
     * 监听订单创建事件
     */
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("处理订单创建事件: {}", event);
        
        String content = String.format("新订单 %s 已创建，桌台号: %s", 
                event.getOrderNo(), event.getTableName());
        
        notificationService.sendOrderNotification(
                "ORDER_CREATED", 
                event.getOrderId(), 
                event.getTableId(), 
                content
        );
    }
    
    /**
     * 监听订单状态变更事件
     */
    @EventListener
    public void handleOrderStatusChanged(OrderStatusChangedEvent event) {
        log.info("处理订单状态变更事件: {}", event);
        
        String content = String.format("订单 %s 状态已更新为: %s", 
                event.getOrderNo(), event.getNewStatusDescription());
        
        notificationService.sendOrderNotification(
                "ORDER_" + event.getNewStatus(), 
                event.getOrderId(), 
                event.getTableId(), 
                content
        );
    }
    
    /**
     * 监听支付成功事件
     */
    @EventListener
    public void handlePaymentSuccess(PaymentSuccessEvent event) {
        log.info("处理支付成功事件: {}", event);
        
        String content = String.format("订单 %s 支付成功，金额: ¥%.2f", 
                event.getOrderNo(), event.getAmount());
        
        notificationService.sendPaymentNotification(
                "PAYMENT_SUCCESS", 
                event.getOrderId(), 
                content
        );
    }
    
    /**
     * 订单创建事件
     */
    public static class OrderCreatedEvent {
        private Long orderId;
        private String orderNo;
        private Long tableId;
        private String tableName;
        
        // 构造函数、getter、setter
        public OrderCreatedEvent(Long orderId, String orderNo, Long tableId, String tableName) {
            this.orderId = orderId;
            this.orderNo = orderNo;
            this.tableId = tableId;
            this.tableName = tableName;
        }
        
        public Long getOrderId() { return orderId; }
        public String getOrderNo() { return orderNo; }
        public Long getTableId() { return tableId; }
        public String getTableName() { return tableName; }
        
        @Override
        public String toString() {
            return String.format("OrderCreatedEvent{orderId=%d, orderNo='%s', tableId=%d, tableName='%s'}", 
                    orderId, orderNo, tableId, tableName);
        }
    }
    
    /**
     * 订单状态变更事件
     */
    public static class OrderStatusChangedEvent {
        private Long orderId;
        private String orderNo;
        private Long tableId;
        private String oldStatus;
        private String newStatus;
        private String newStatusDescription;
        
        public OrderStatusChangedEvent(Long orderId, String orderNo, Long tableId, 
                                     String oldStatus, String newStatus, String newStatusDescription) {
            this.orderId = orderId;
            this.orderNo = orderNo;
            this.tableId = tableId;
            this.oldStatus = oldStatus;
            this.newStatus = newStatus;
            this.newStatusDescription = newStatusDescription;
        }
        
        public Long getOrderId() { return orderId; }
        public String getOrderNo() { return orderNo; }
        public Long getTableId() { return tableId; }
        public String getOldStatus() { return oldStatus; }
        public String getNewStatus() { return newStatus; }
        public String getNewStatusDescription() { return newStatusDescription; }
        
        @Override
        public String toString() {
            return String.format("OrderStatusChangedEvent{orderId=%d, orderNo='%s', oldStatus='%s', newStatus='%s'}", 
                    orderId, orderNo, oldStatus, newStatus);
        }
    }
    
    /**
     * 支付成功事件
     */
    public static class PaymentSuccessEvent {
        private Long orderId;
        private String orderNo;
        private Double amount;
        
        public PaymentSuccessEvent(Long orderId, String orderNo, Double amount) {
            this.orderId = orderId;
            this.orderNo = orderNo;
            this.amount = amount;
        }
        
        public Long getOrderId() { return orderId; }
        public String getOrderNo() { return orderNo; }
        public Double getAmount() { return amount; }
        
        @Override
        public String toString() {
            return String.format("PaymentSuccessEvent{orderId=%d, orderNo='%s', amount=%.2f}", 
                    orderId, orderNo, amount);
        }
    }
}