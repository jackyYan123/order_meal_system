-- 添加订单、支付、通知相关表

-- 支付记录表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_no VARCHAR(50) NOT NULL UNIQUE COMMENT '支付流水号',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    order_no VARCHAR(50) COMMENT '订单号',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式：WECHAT-微信支付, ALIPAY-支付宝, CASH-现金',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '支付状态：PENDING-待支付, SUCCESS-支付成功, FAILED-支付失败, REFUNDED-已退款',
    third_party_transaction_id VARCHAR(100) COMMENT '第三方支付平台交易号',
    paid_time TIMESTAMP NULL COMMENT '支付完成时间',
    refund_time TIMESTAMP NULL COMMENT '退款时间',
    refund_amount DECIMAL(10,2) COMMENT '退款金额',
    failure_reason VARCHAR(500) COMMENT '失败原因',
    remarks VARCHAR(500) COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_order_id (order_id),
    INDEX idx_payment_no (payment_no),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT '支付记录表';

-- 通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type VARCHAR(50) NOT NULL COMMENT '通知类型：ORDER_CREATED-新订单, ORDER_CONFIRMED-订单确认, ORDER_READY-订单完成, PAYMENT_SUCCESS-支付成功',
    recipient_type VARCHAR(20) NOT NULL COMMENT '接收者类型：CUSTOMER-顾客, STAFF-员工, CHEF-厨师, ALL-所有人',
    recipient_id BIGINT COMMENT '接收者ID（可选，用于指定特定用户）',
    title VARCHAR(200) NOT NULL COMMENT '通知标题',
    content TEXT NOT NULL COMMENT '通知内容',
    order_id BIGINT COMMENT '关联的订单ID',
    table_id BIGINT COMMENT '关联的桌台ID',
    is_read BOOLEAN DEFAULT FALSE COMMENT '是否已读',
    read_time TIMESTAMP NULL COMMENT '阅读时间',
    channel VARCHAR(20) DEFAULT 'WEBSOCKET' COMMENT '通知渠道：WEBSOCKET-WebSocket, SMS-短信, EMAIL-邮件',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '发送状态：PENDING-待发送, SENT-已发送, FAILED-发送失败',
    sent_time TIMESTAMP NULL COMMENT '发送时间',
    failure_reason VARCHAR(500) COMMENT '失败原因',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_recipient (recipient_type, recipient_id),
    INDEX idx_order_id (order_id),
    INDEX idx_table_id (table_id),
    INDEX idx_is_read (is_read),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) COMMENT '通知表';

-- 更新订单表，添加支付相关字段
ALTER TABLE orders 
ADD COLUMN payment_status VARCHAR(20) DEFAULT 'UNPAID' COMMENT '支付状态：UNPAID-未支付, PAID-已支付, REFUNDED-已退款' AFTER total_amount,
ADD COLUMN payment_method VARCHAR(20) COMMENT '支付方式：WECHAT-微信支付, ALIPAY-支付宝, CASH-现金' AFTER payment_status,
ADD COLUMN payment_time TIMESTAMP NULL COMMENT '支付时间' AFTER payment_method,
ADD COLUMN estimated_time TIMESTAMP NULL COMMENT '预计完成时间' AFTER payment_time,
ADD COLUMN completed_time TIMESTAMP NULL COMMENT '实际完成时间' AFTER estimated_time,
ADD COLUMN cancel_reason VARCHAR(500) COMMENT '取消原因' AFTER remarks;

-- 添加索引
ALTER TABLE orders ADD INDEX idx_payment_status (payment_status);
ALTER TABLE orders ADD INDEX idx_estimated_time (estimated_time);
ALTER TABLE orders ADD INDEX idx_completed_time (completed_time);

-- 插入测试数据
INSERT INTO payments (payment_no, order_id, order_no, amount, payment_method, status, created_at) VALUES
('PAY20231201001', 1, 'ORD20231201001', 88.00, 'WECHAT', 'SUCCESS', '2023-12-01 12:30:00'),
('PAY20231201002', 2, 'ORD20231201002', 156.00, 'CASH', 'SUCCESS', '2023-12-01 13:15:00');

INSERT INTO notifications (type, recipient_type, title, content, order_id, table_id, is_read, status, created_at) VALUES
('ORDER_CREATED', 'STAFF', '新订单', '桌台T001有新订单，请及时处理', 1, 1, FALSE, 'SENT', '2023-12-01 12:00:00'),
('ORDER_READY', 'CUSTOMER', '订单完成', '您的订单已制作完成，请取餐', 1, 1, FALSE, 'SENT', '2023-12-01 12:25:00'),
('PAYMENT_SUCCESS', 'CUSTOMER', '支付成功', '订单支付成功，金额：¥88.00', 1, 1, TRUE, 'SENT', '2023-12-01 12:30:00');