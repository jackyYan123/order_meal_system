-- 修复订单项表结构，添加缺失字段
ALTER TABLE order_items 
ADD COLUMN dish_name VARCHAR(100) COMMENT '菜品名称（冗余字段，防止菜品信息变更影响历史订单）' AFTER dish_id,
ADD COLUMN dish_price DECIMAL(10,2) COMMENT '菜品价格（下单时的价格）' AFTER dish_name,
ADD COLUMN special_requests TEXT COMMENT '特殊要求' AFTER subtotal;

-- 重命名字段以保持一致性
ALTER TABLE order_items 
CHANGE COLUMN unit_price dish_price_old DECIMAL(10,2) NOT NULL COMMENT '旧的单价字段（待删除）';

-- 更新现有数据（如果有的话）
UPDATE order_items SET 
    dish_name = '默认菜品名称',
    dish_price = dish_price_old,
    special_requests = ''
WHERE dish_name IS NULL;

-- 删除旧字段
ALTER TABLE order_items DROP COLUMN dish_price_old;

-- 添加索引
CREATE INDEX idx_order_items_dish_name ON order_items(dish_name);