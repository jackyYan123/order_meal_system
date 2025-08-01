-- 插入更多订单和订单项示例数据
USE restaurant_dev;

-- 插入更多订单数据
INSERT INTO orders (order_number, table_id, customer_id, staff_user_id, total_amount, status, payment_status, payment_method, remark, created_at, updated_at) VALUES 
-- 今日新增订单
('ORDER20250724006', 2, 1, 2, 127.00, 'COMPLETED', 'PAID', 'WECHAT', '要微辣', '2025-07-24 17:30:00', '2025-07-24 18:15:00'),
('ORDER20250724007', 5, 3, 2, 89.00, 'READY', 'PAID', 'ALIPAY', '汤要少盐', '2025-07-24 18:45:00', '2025-07-24 19:20:00'),
('ORDER20250724008', 3, 2, 2, 156.00, 'PREPARING', 'PAID', 'WECHAT', '菜品要热', '2025-07-24 19:15:00', '2025-07-24 19:25:00'),
('ORDER20250724009', 1, 1, 2, 73.00, 'CONFIRMED', 'PAID', 'CASH', '', '2025-07-24 19:45:00', '2025-07-24 19:47:00'),
('ORDER20250724010', 4, 3, 2, 64.00, 'PENDING', 'UNPAID', NULL, '不要香菜', '2025-07-24 20:10:00', '2025-07-24 20:10:00'),

-- 昨日新增订单
('ORDER20250723006', 3, 2, 2, 98.00, 'COMPLETED', 'PAID', 'WECHAT', '多加点辣椒', '2025-07-23 14:30:00', '2025-07-23 15:20:00'),
('ORDER20250723007', 1, 1, 2, 45.00, 'COMPLETED', 'PAID', 'CASH', '', '2025-07-23 15:45:00', '2025-07-23 16:30:00'),
('ORDER20250723008', 4, 3, 2, 112.00, 'COMPLETED', 'PAID', 'ALIPAY', '汤要浓一点', '2025-07-23 17:00:00', '2025-07-23 17:50:00'),
('ORDER20250723009', 2, 2, 2, 87.00, 'COMPLETED', 'PAID', 'WECHAT', '少油', '2025-07-23 20:15:00', '2025-07-23 21:00:00'),
('ORDER20250723010', 5, 1, 2, 36.00, 'CANCELLED', 'UNPAID', NULL, '客户临时有事', '2025-07-23 21:30:00', '2025-07-23 21:35:00'),

-- 前日新增订单
('ORDER20250722004', 2, 3, 2, 134.00, 'COMPLETED', 'PAID', 'WECHAT', '', '2025-07-22 11:30:00', '2025-07-22 12:20:00'),
('ORDER20250722005', 1, 1, 2, 78.00, 'COMPLETED', 'PAID', 'CASH', '不要葱', '2025-07-22 14:45:00', '2025-07-22 15:30:00'),
('ORDER20250722006', 5, 2, 2, 91.00, 'COMPLETED', 'PAID', 'ALIPAY', '', '2025-07-22 19:00:00', '2025-07-22 19:45:00');

-- 插入对应的订单项数据
INSERT INTO order_items (order_id, dish_id, quantity, unit_price, subtotal, created_at, updated_at) VALUES 
-- ORDER20250724006 的订单项 (总计127.00)
(14, 3, 1, 35.00, 35.00, '2025-07-24 17:30:00', '2025-07-24 17:30:00'), -- 红烧肉
(14, 6, 1, 30.00, 30.00, '2025-07-24 17:30:00', '2025-07-24 17:30:00'), -- 回锅肉
(14, 8, 1, 25.00, 25.00, '2025-07-24 17:30:00', '2025-07-24 17:30:00'), -- 口水鸡
(14, 12, 1, 28.00, 28.00, '2025-07-24 17:30:00', '2025-07-24 17:30:00'), -- 冬瓜排骨汤
(14, 17, 1, 15.00, 15.00, '2025-07-24 17:30:00', '2025-07-24 17:30:00'), -- 鲜榨橙汁

-- ORDER20250724007 的订单项 (总计89.00)
(15, 1, 2, 28.00, 56.00, '2025-07-24 18:45:00', '2025-07-24 18:45:00'), -- 宫保鸡丁 x2
(15, 7, 1, 12.00, 12.00, '2025-07-24 18:45:00', '2025-07-24 18:45:00'), -- 凉拌黄瓜
(15, 13, 1, 12.00, 12.00, '2025-07-24 18:45:00', '2025-07-24 18:45:00'), -- 紫菜蛋花汤
(15, 21, 1, 8.00, 8.00, '2025-07-24 18:45:00', '2025-07-24 18:45:00'),   -- 可乐

-- ORDER20250724008 的订单项 (总计156.00)
(16, 3, 1, 35.00, 35.00, '2025-07-24 19:15:00', '2025-07-24 19:15:00'), -- 红烧肉
(16, 4, 1, 32.00, 32.00, '2025-07-24 19:15:00', '2025-07-24 19:15:00'), -- 糖醋里脊
(16, 5, 1, 26.00, 26.00, '2025-07-24 19:15:00', '2025-07-24 19:15:00'), -- 鱼香肉丝
(16, 6, 1, 30.00, 30.00, '2025-07-24 19:15:00', '2025-07-24 19:15:00'), -- 回锅肉
(16, 11, 1, 15.00, 15.00, '2025-07-24 19:15:00', '2025-07-24 19:15:00'), -- 西红柿鸡蛋汤
(16, 18, 1, 18.00, 18.00, '2025-07-24 19:15:00', '2025-07-24 19:15:00'), -- 柠檬蜂蜜茶

-- ORDER20250724009 的订单项 (总计73.00)
(17, 2, 1, 18.00, 18.00, '2025-07-24 19:45:00', '2025-07-24 19:45:00'), -- 麻婆豆腐
(17, 5, 1, 26.00, 26.00, '2025-07-24 19:45:00', '2025-07-24 19:45:00'), -- 鱼香肉丝
(17, 10, 1, 15.00, 15.00, '2025-07-24 19:45:00', '2025-07-24 19:45:00'), -- 凉拌木耳
(17, 20, 1, 14.00, 14.00, '2025-07-24 19:45:00', '2025-07-24 19:45:00'), -- 酸梅汤

-- ORDER20250724010 的订单项 (总计64.00)
(18, 1, 1, 28.00, 28.00, '2025-07-24 20:10:00', '2025-07-24 20:10:00'), -- 宫保鸡丁
(18, 9, 1, 10.00, 10.00, '2025-07-24 20:10:00', '2025-07-24 20:10:00'), -- 拍黄瓜
(18, 14, 1, 18.00, 18.00, '2025-07-24 20:10:00', '2025-07-24 20:10:00'), -- 酸辣汤
(18, 22, 1, 8.00, 8.00, '2025-07-24 20:10:00', '2025-07-24 20:10:00'),   -- 雪碧

-- ORDER20250723006 的订单项 (总计98.00)
(19, 3, 1, 35.00, 35.00, '2025-07-23 14:30:00', '2025-07-23 14:30:00'), -- 红烧肉
(19, 4, 1, 32.00, 32.00, '2025-07-23 14:30:00', '2025-07-23 14:30:00'), -- 糖醋里脊
(19, 8, 1, 25.00, 25.00, '2025-07-23 14:30:00', '2025-07-23 14:30:00'), -- 口水鸡
(19, 19, 1, 12.00, 12.00, '2025-07-23 14:30:00', '2025-07-23 14:30:00'), -- 绿豆汤

-- ORDER20250723007 的订单项 (总计45.00)
(20, 2, 1, 18.00, 18.00, '2025-07-23 15:45:00', '2025-07-23 15:45:00'), -- 麻婆豆腐
(20, 7, 1, 12.00, 12.00, '2025-07-23 15:45:00', '2025-07-23 15:45:00'), -- 凉拌黄瓜
(20, 11, 1, 15.00, 15.00, '2025-07-23 15:45:00', '2025-07-23 15:45:00'), -- 西红柿鸡蛋汤

-- ORDER20250723008 的订单项 (总计112.00)
(21, 1, 1, 28.00, 28.00, '2025-07-23 17:00:00', '2025-07-23 17:00:00'), -- 宫保鸡丁
(21, 5, 1, 26.00, 26.00, '2025-07-23 17:00:00', '2025-07-23 17:00:00'), -- 鱼香肉丝
(21, 6, 1, 30.00, 30.00, '2025-07-23 17:00:00', '2025-07-23 17:00:00'), -- 回锅肉
(21, 12, 1, 28.00, 28.00, '2025-07-23 17:00:00', '2025-07-23 17:00:00'), -- 冬瓜排骨汤

-- ORDER20250723009 的订单项 (总计87.00)
(22, 3, 1, 35.00, 35.00, '2025-07-23 20:15:00', '2025-07-23 20:15:00'), -- 红烧肉
(22, 4, 1, 32.00, 32.00, '2025-07-23 20:15:00', '2025-07-23 20:15:00'), -- 糖醋里脊
(22, 9, 1, 10.00, 10.00, '2025-07-23 20:15:00', '2025-07-23 20:15:00'), -- 拍黄瓜
(22, 17, 1, 15.00, 15.00, '2025-07-23 20:15:00', '2025-07-23 20:15:00'), -- 鲜榨橙汁

-- ORDER20250723010 的订单项 (总计36.00 - 已取消)
(23, 2, 1, 18.00, 18.00, '2025-07-23 21:30:00', '2025-07-23 21:30:00'), -- 麻婆豆腐
(23, 18, 1, 18.00, 18.00, '2025-07-23 21:30:00', '2025-07-23 21:30:00'), -- 柠檬蜂蜜茶

-- ORDER20250722004 的订单项 (总计134.00)
(24, 3, 2, 35.00, 70.00, '2025-07-22 11:30:00', '2025-07-22 11:30:00'), -- 红烧肉 x2
(24, 1, 1, 28.00, 28.00, '2025-07-22 11:30:00', '2025-07-22 11:30:00'), -- 宫保鸡丁
(24, 8, 1, 25.00, 25.00, '2025-07-22 11:30:00', '2025-07-22 11:30:00'), -- 口水鸡
(24, 13, 1, 12.00, 12.00, '2025-07-22 11:30:00', '2025-07-22 11:30:00'), -- 紫菜蛋花汤

-- ORDER20250722005 的订单项 (总计78.00)
(25, 5, 1, 26.00, 26.00, '2025-07-22 14:45:00', '2025-07-22 14:45:00'), -- 鱼香肉丝
(25, 6, 1, 30.00, 30.00, '2025-07-22 14:45:00', '2025-07-22 14:45:00'), -- 回锅肉
(25, 10, 1, 15.00, 15.00, '2025-07-22 14:45:00', '2025-07-22 14:45:00'), -- 凉拌木耳
(25, 21, 1, 8.00, 8.00, '2025-07-22 14:45:00', '2025-07-22 14:45:00'),   -- 可乐

-- ORDER20250722006 的订单项 (总计91.00)
(26, 4, 1, 32.00, 32.00, '2025-07-22 19:00:00', '2025-07-22 19:00:00'), -- 糖醋里脊
(26, 2, 1, 18.00, 18.00, '2025-07-22 19:00:00', '2025-07-22 19:00:00'), -- 麻婆豆腐
(26, 11, 1, 28.00, 28.00, '2025-07-22 19:00:00', '2025-07-22 19:00:00'), -- 蒜泥白肉
(26, 14, 1, 18.00, 18.00, '2025-07-22 19:00:00', '2025-07-22 19:00:00'), -- 酸辣汤

-- 插入对应的支付记录
INSERT INTO payments (payment_no, order_id, order_no, amount, payment_method, status, third_party_transaction_id, paid_time, created_at, updated_at) VALUES
('PAY20250724006', 14, 'ORDER20250724006', 127.00, 'WECHAT', 'SUCCESS', 'wx_20250724173500001', '2025-07-24 17:35:00', '2025-07-24 17:35:00', '2025-07-24 17:35:00'),
('PAY20250724007', 15, 'ORDER20250724007', 89.00, 'ALIPAY', 'SUCCESS', 'ali_20250724184800001', '2025-07-24 18:48:00', '2025-07-24 18:48:00', '2025-07-24 18:48:00'),
('PAY20250724008', 16, 'ORDER20250724008', 156.00, 'WECHAT', 'SUCCESS', 'wx_20250724191800001', '2025-07-24 19:18:00', '2025-07-24 19:18:00', '2025-07-24 19:18:00'),
('PAY20250724009', 17, 'ORDER20250724009', 73.00, 'CASH', 'SUCCESS', NULL, '2025-07-24 19:47:00', '2025-07-24 19:47:00', '2025-07-24 19:47:00'),

('PAY20250723006', 19, 'ORDER20250723006', 98.00, 'WECHAT', 'SUCCESS', 'wx_20250723143200001', '2025-07-23 14:32:00', '2025-07-23 14:32:00', '2025-07-23 14:32:00'),
('PAY20250723007', 20, 'ORDER20250723007', 45.00, 'CASH', 'SUCCESS', NULL, '2025-07-23 15:47:00', '2025-07-23 15:47:00', '2025-07-23 15:47:00'),
('PAY20250723008', 21, 'ORDER20250723008', 112.00, 'ALIPAY', 'SUCCESS', 'ali_20250723170300001', '2025-07-23 17:03:00', '2025-07-23 17:03:00', '2025-07-23 17:03:00'),
('PAY20250723009', 22, 'ORDER20250723009', 87.00, 'WECHAT', 'SUCCESS', 'wx_20250723201800001', '2025-07-23 20:18:00', '2025-07-23 20:18:00', '2025-07-23 20:18:00'),

('PAY20250722004', 24, 'ORDER20250722004', 134.00, 'WECHAT', 'SUCCESS', 'wx_20250722113300001', '2025-07-22 11:33:00', '2025-07-22 11:33:00', '2025-07-22 11:33:00'),
('PAY20250722005', 25, 'ORDER20250722005', 78.00, 'CASH', 'SUCCESS', NULL, '2025-07-22 14:47:00', '2025-07-22 14:47:00', '2025-07-22 14:47:00'),
('PAY20250722006', 26, 'ORDER20250722006', 91.00, 'ALIPAY', 'SUCCESS', 'ali_20250722190300001', '2025-07-22 19:03:00', '2025-07-22 19:03:00', '2025-07-22 19:03:00');

-- 插入相关通知记录
INSERT INTO notifications (type, recipient_type, title, content, order_id, table_id, is_read, status, sent_time, created_at, updated_at) VALUES
-- 今日订单通知
('ORDER_CREATED', 'STAFF', '新订单', '桌台T002有新订单ORDER20250724006，请及时处理', 14, 2, TRUE, 'SENT', '2025-07-24 17:30:00', '2025-07-24 17:30:00', '2025-07-24 17:30:00'),
('PAYMENT_SUCCESS', 'CUSTOMER', '支付成功', '订单ORDER20250724006支付成功，金额：¥127.00', 14, 2, TRUE, 'SENT', '2025-07-24 17:35:00', '2025-07-24 17:35:00', '2025-07-24 17:35:00'),
('ORDER_READY', 'CUSTOMER', '订单完成', '您的订单ORDER20250724006已制作完成，请取餐', 14, 2, FALSE, 'SENT', '2025-07-24 18:15:00', '2025-07-24 18:15:00', '2025-07-24 18:15:00'),

('ORDER_CREATED', 'STAFF', '新订单', '桌台T005有新订单ORDER20250724007，请及时处理', 15, 5, TRUE, 'SENT', '2025-07-24 18:45:00', '2025-07-24 18:45:00', '2025-07-24 18:45:00'),
('PAYMENT_SUCCESS', 'CUSTOMER', '支付成功', '订单ORDER20250724007支付成功，金额：¥89.00', 15, 5, TRUE, 'SENT', '2025-07-24 18:48:00', '2025-07-24 18:48:00', '2025-07-24 18:48:00'),

('ORDER_CREATED', 'STAFF', '新订单', '桌台T003有新订单ORDER20250724008，请及时处理', 16, 3, TRUE, 'SENT', '2025-07-24 19:15:00', '2025-07-24 19:15:00', '2025-07-24 19:15:00'),
('PAYMENT_SUCCESS', 'CUSTOMER', '支付成功', '订单ORDER20250724008支付成功，金额：¥156.00', 16, 3, TRUE, 'SENT', '2025-07-24 19:18:00', '2025-07-24 19:18:00', '2025-07-24 19:18:00'),

-- 昨日订单通知
('ORDER_CREATED', 'STAFF', '新订单', '桌台T003有新订单ORDER20250723006，请及时处理', 19, 3, TRUE, 'SENT', '2025-07-23 14:30:00', '2025-07-23 14:30:00', '2025-07-23 14:30:00'),
('ORDER_READY', 'CUSTOMER', '订单完成', '您的订单ORDER20250723006已制作完成，请取餐', 19, 3, TRUE, 'SENT', '2025-07-23 15:20:00', '2025-07-23 15:20:00', '2025-07-23 15:20:00'),

('ORDER_CANCELLED', 'STAFF', '订单取消', '订单ORDER20250723010已被取消，原因：客户临时有事', 23, 5, TRUE, 'SENT', '2025-07-23 21:35:00', '2025-07-23 21:35:00', '2025-07-23 21:35:00');

-- 更新顾客统计信息
UPDATE customers SET 
    total_orders = (SELECT COUNT(*) FROM orders WHERE customer_id = customers.id AND status != 'CANCELLED'),
    total_amount = (SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE customer_id = customers.id AND status != 'CANCELLED')
WHERE id IN (1, 2, 3);