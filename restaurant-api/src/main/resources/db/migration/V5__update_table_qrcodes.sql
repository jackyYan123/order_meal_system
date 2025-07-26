-- 为现有桌台生成二维码内容
USE restaurant_dev;

-- 更新桌台二维码内容
UPDATE tables SET qr_code = '{"tableId":1,"tableName":"T001","capacity":4}' WHERE table_number = 'T001';
UPDATE tables SET qr_code = '{"tableId":2,"tableName":"T002","capacity":6}' WHERE table_number = 'T002';
UPDATE tables SET qr_code = '{"tableId":3,"tableName":"T003","capacity":2}' WHERE table_number = 'T003';
UPDATE tables SET qr_code = '{"tableId":4,"tableName":"T004","capacity":8}' WHERE table_number = 'T004';
UPDATE tables SET qr_code = '{"tableId":5,"tableName":"T005","capacity":4}' WHERE table_number = 'T005';