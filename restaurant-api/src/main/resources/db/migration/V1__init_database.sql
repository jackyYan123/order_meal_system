-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS restaurant_dev 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE restaurant_dev;

-- 系统用户表（员工）
CREATE TABLE IF NOT EXISTS staff_users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码',
    role VARCHAR(20) NOT NULL DEFAULT 'STAFF' COMMENT '角色：ADMIN, STAFF, CHEF',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '系统用户表（员工）';

-- 顾客表
CREATE TABLE IF NOT EXISTS customers (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    phone VARCHAR(20) COMMENT '手机号（可选，用于会员识别）',
    name VARCHAR(50) COMMENT '姓名（可选）',
    wechat_openid VARCHAR(100) COMMENT '微信小程序OpenID',
    member_level VARCHAR(20) DEFAULT 'NORMAL' COMMENT '会员等级：NORMAL, VIP, GOLD',
    total_orders INT DEFAULT 0 COMMENT '总订单数',
    total_amount DECIMAL(10,2) DEFAULT 0.00 COMMENT '总消费金额',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '顾客表';

-- 分类表
CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类描述',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    is_active BOOLEAN DEFAULT TRUE COMMENT '是否激活',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '菜品分类表';

-- 菜品表
CREATE TABLE IF NOT EXISTS dishes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '菜品名称',
    description TEXT COMMENT '菜品描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    image_url VARCHAR(500) COMMENT '图片URL',
    stock INT DEFAULT 0 COMMENT '库存数量',
    is_available BOOLEAN DEFAULT TRUE COMMENT '是否可用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (category_id) REFERENCES categories(id)
) COMMENT '菜品表';

-- 桌台表
CREATE TABLE IF NOT EXISTS tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_number VARCHAR(20) NOT NULL UNIQUE COMMENT '桌台号',
    capacity INT NOT NULL DEFAULT 4 COMMENT '容纳人数',
    status VARCHAR(20) DEFAULT 'AVAILABLE' COMMENT '状态：AVAILABLE, OCCUPIED, RESERVED',
    qr_code VARCHAR(500) COMMENT '二维码',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '桌台表';

-- 订单表
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    table_id BIGINT NOT NULL COMMENT '桌台ID',
    customer_id BIGINT COMMENT '顾客ID（可选，匿名顾客可为空）',
    staff_user_id BIGINT COMMENT '处理订单的员工ID（可选）',
    total_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING, CONFIRMED, PREPARING, READY, COMPLETED, CANCELLED',
    remark TEXT COMMENT '备注',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (table_id) REFERENCES tables(id),
    FOREIGN KEY (customer_id) REFERENCES customers(id),
    FOREIGN KEY (staff_user_id) REFERENCES staff_users(id)
) COMMENT '订单表';

-- 订单项表
CREATE TABLE IF NOT EXISTS order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    dish_id BIGINT NOT NULL COMMENT '菜品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '数量',
    unit_price DECIMAL(10,2) NOT NULL COMMENT '单价',
    subtotal DECIMAL(10,2) NOT NULL COMMENT '小计',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (dish_id) REFERENCES dishes(id)
) COMMENT '订单项表';

-- 支付表
CREATE TABLE IF NOT EXISTS payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL COMMENT '订单ID',
    payment_method VARCHAR(20) NOT NULL COMMENT '支付方式：WECHAT, ALIPAY, CASH',
    amount DECIMAL(10,2) NOT NULL COMMENT '支付金额',
    status VARCHAR(20) DEFAULT 'PENDING' COMMENT '状态：PENDING, SUCCESS, FAILED, REFUNDED',
    transaction_id VARCHAR(100) COMMENT '交易ID',
    paid_at TIMESTAMP NULL COMMENT '支付时间',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (order_id) REFERENCES orders(id)
) COMMENT '支付表';

-- 创建索引
CREATE INDEX idx_orders_table_id ON orders(table_id);
CREATE INDEX idx_orders_customer_id ON orders(customer_id);
CREATE INDEX idx_orders_staff_user_id ON orders(staff_user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_dishes_category_id ON dishes(category_id);
CREATE INDEX idx_dishes_is_available ON dishes(is_available);
CREATE INDEX idx_payments_order_id ON payments(order_id);
CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_customers_wechat_openid ON customers(wechat_openid);
CREATE INDEX idx_staff_users_username ON staff_users(username);

-- 插入初始数据
INSERT INTO staff_users (username, password, role, real_name) VALUES 
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'ADMIN', '系统管理员'),
('staff', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'STAFF', '服务员'),
('chef', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDi', 'CHEF', '厨师');

-- 插入一些示例顾客数据
INSERT INTO customers (phone, name, member_level) VALUES 
('13800138001', '张三', 'NORMAL'),
('13800138002', '李四', 'VIP'),
('13800138003', '王五', 'NORMAL');

INSERT INTO categories (name, description, sort_order) VALUES 
('热菜', '各种热菜', 1),
('凉菜', '各种凉菜', 2),
('汤类', '各种汤品', 3),
('饮品', '各种饮品', 4);

-- 角色表
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_code VARCHAR(50) NOT NULL UNIQUE COMMENT '角色代码',
    description TEXT COMMENT '角色描述',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT '角色表';

-- 权限表
CREATE TABLE IF NOT EXISTS permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    permission_name VARCHAR(100) NOT NULL COMMENT '权限名称',
    permission_code VARCHAR(100) NOT NULL UNIQUE COMMENT '权限代码',
    description TEXT COMMENT '权限描述',
    permission_type VARCHAR(20) DEFAULT 'API' COMMENT '权限类型：MENU-菜单权限，BUTTON-按钮权限，API-接口权限',
    resource_path VARCHAR(200) COMMENT '资源路径',
    parent_id BIGINT COMMENT '父权限ID',
    is_enabled BOOLEAN DEFAULT TRUE COMMENT '是否启用',
    sort_order INT DEFAULT 0 COMMENT '排序顺序',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (parent_id) REFERENCES permissions(id)
) COMMENT '权限表';

-- 角色权限关联表
CREATE TABLE IF NOT EXISTS role_permissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES roles(id),
    FOREIGN KEY (permission_id) REFERENCES permissions(id),
    UNIQUE KEY uk_role_permission (role_id, permission_id)
) COMMENT '角色权限关联表';

-- 用户角色关联表
CREATE TABLE IF NOT EXISTS user_roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    user_type VARCHAR(20) NOT NULL COMMENT '用户类型：STAFF-员工，CUSTOMER-顾客',
    deleted INT DEFAULT 0 COMMENT '逻辑删除标志：0-未删除，1-已删除',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (role_id) REFERENCES roles(id),
    UNIQUE KEY uk_user_role_type (user_id, role_id, user_type)
) COMMENT '用户角色关联表';

-- 权限相关索引
CREATE INDEX idx_permissions_parent_id ON permissions(parent_id);
CREATE INDEX idx_permissions_permission_code ON permissions(permission_code);
CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions(permission_id);
CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);
CREATE INDEX idx_user_roles_user_type ON user_roles(user_type);

-- 插入基础角色数据
INSERT INTO roles (role_name, role_code, description, sort_order) VALUES 
('系统管理员', 'ADMIN', '系统管理员，拥有所有权限', 1),
('餐厅经理', 'MANAGER', '餐厅经理，拥有除系统管理外的所有权限', 2),
('服务员', 'WAITER', '服务员，负责订单管理和桌台管理', 3),
('厨师', 'CHEF', '厨师，负责订单制作状态管理', 4),
('顾客', 'CUSTOMER', '顾客，可以浏览菜单和下单', 5);

-- 插入基础权限数据
INSERT INTO permissions (permission_name, permission_code, description, permission_type, resource_path, sort_order) VALUES 
-- 系统管理权限
('系统管理', 'SYSTEM_MANAGE', '系统管理权限', 'MENU', '/admin/system', 1),
('用户管理', 'USER_MANAGE', '用户管理权限', 'MENU', '/admin/users', 2),
('角色管理', 'ROLE_MANAGE', '角色管理权限', 'MENU', '/admin/roles', 3),
('权限管理', 'PERMISSION_MANAGE', '权限管理权限', 'MENU', '/admin/permissions', 4),

-- 菜品管理权限
('菜品管理', 'DISH_MANAGE', '菜品管理权限', 'MENU', '/admin/dishes', 10),
('菜品查看', 'DISH_VIEW', '菜品查看权限', 'API', '/api/dishes', 11),
('菜品创建', 'DISH_CREATE', '菜品创建权限', 'API', '/api/dishes', 12),
('菜品更新', 'DISH_UPDATE', '菜品更新权限', 'API', '/api/dishes/*', 13),
('菜品删除', 'DISH_DELETE', '菜品删除权限', 'API', '/api/dishes/*', 14),

-- 分类管理权限
('分类管理', 'CATEGORY_MANAGE', '分类管理权限', 'MENU', '/admin/categories', 20),
('分类查看', 'CATEGORY_VIEW', '分类查看权限', 'API', '/api/categories', 21),
('分类创建', 'CATEGORY_CREATE', '分类创建权限', 'API', '/api/categories', 22),
('分类更新', 'CATEGORY_UPDATE', '分类更新权限', 'API', '/api/categories/*', 23),
('分类删除', 'CATEGORY_DELETE', '分类删除权限', 'API', '/api/categories/*', 24),

-- 订单管理权限
('订单管理', 'ORDER_MANAGE', '订单管理权限', 'MENU', '/admin/orders', 30),
('订单查看', 'ORDER_VIEW', '订单查看权限', 'API', '/api/orders', 31),
('订单创建', 'ORDER_CREATE', '订单创建权限', 'API', '/api/orders', 32),
('订单更新', 'ORDER_UPDATE', '订单更新权限', 'API', '/api/orders/*', 33),
('订单状态更新', 'ORDER_UPDATE_STATUS', '订单状态更新权限', 'API', '/api/orders/*/status', 34),
('订单取消', 'ORDER_CANCEL', '订单取消权限', 'API', '/api/orders/*/cancel', 35),

-- 桌台管理权限
('桌台管理', 'TABLE_MANAGE', '桌台管理权限', 'MENU', '/admin/tables', 40),
('桌台查看', 'TABLE_VIEW', '桌台查看权限', 'API', '/api/tables', 41),
('桌台状态更新', 'TABLE_UPDATE_STATUS', '桌台状态更新权限', 'API', '/api/tables/*/status', 42),

-- 支付管理权限
('支付管理', 'PAYMENT_MANAGE', '支付管理权限', 'MENU', '/admin/payments', 50),
('支付查看', 'PAYMENT_VIEW', '支付查看权限', 'API', '/api/payments', 51),
('支付处理', 'PAYMENT_PROCESS', '支付处理权限', 'API', '/api/payments/process', 52),

-- 顾客权限
('菜单浏览', 'MENU_VIEW', '菜单浏览权限', 'API', '/api/menu', 60),
('订单查看（自己的）', 'ORDER_VIEW_OWN', '查看自己的订单权限', 'API', '/api/orders/my', 61);

-- 为角色分配权限
-- 管理员拥有所有权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p WHERE r.role_code = 'ADMIN';

-- 经理拥有除系统管理外的所有权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_code = 'MANAGER' AND p.permission_code NOT LIKE 'SYSTEM_%' AND p.permission_code NOT LIKE 'USER_%' AND p.permission_code NOT LIKE 'ROLE_%' AND p.permission_code NOT LIKE 'PERMISSION_%';

-- 服务员权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_code = 'WAITER' AND p.permission_code IN (
    'ORDER_VIEW', 'ORDER_CREATE', 'ORDER_UPDATE', 'ORDER_UPDATE_STATUS', 'ORDER_CANCEL',
    'TABLE_VIEW', 'TABLE_UPDATE_STATUS',
    'DISH_VIEW', 'CATEGORY_VIEW', 'MENU_VIEW'
);

-- 厨师权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_code = 'CHEF' AND p.permission_code IN (
    'ORDER_VIEW', 'ORDER_UPDATE_STATUS',
    'DISH_VIEW', 'CATEGORY_VIEW'
);

-- 顾客权限
INSERT INTO role_permissions (role_id, permission_id) 
SELECT r.id, p.id FROM roles r, permissions p 
WHERE r.role_code = 'CUSTOMER' AND p.permission_code IN (
    'MENU_VIEW', 'ORDER_CREATE', 'ORDER_VIEW_OWN'
);

-- 为现有员工分配角色
INSERT INTO user_roles (user_id, role_id, user_type) 
SELECT u.id, r.id, 'STAFF' FROM staff_users u, roles r 
WHERE u.username = 'admin' AND r.role_code = 'ADMIN';

INSERT INTO user_roles (user_id, role_id, user_type) 
SELECT u.id, r.id, 'STAFF' FROM staff_users u, roles r 
WHERE u.username = 'staff' AND r.role_code = 'WAITER';

INSERT INTO user_roles (user_id, role_id, user_type) 
SELECT u.id, r.id, 'STAFF' FROM staff_users u, roles r 
WHERE u.username = 'chef' AND r.role_code = 'CHEF';

-- 为现有顾客分配角色
INSERT INTO user_roles (user_id, role_id, user_type) 
SELECT c.id, r.id, 'CUSTOMER' FROM customers c, roles r 
WHERE r.role_code = 'CUSTOMER';

INSERT INTO tables (table_number, capacity) VALUES 
('T001', 4),
('T002', 6),
('T003', 2),
('T004', 8),
('T005', 4);