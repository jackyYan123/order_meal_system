-- 插入示例菜品数据
USE restaurant_dev;

-- 插入菜品数据
INSERT INTO dishes (category_id, name, description, price, image_url, stock, is_available, sort_order) VALUES 
-- 热菜
(1, '宫保鸡丁', '经典川菜，鸡肉嫩滑，花生香脆，麻辣鲜香', 28.00, '/images/dishes/gongbao_jiding.jpg', 50, TRUE, 1),
(1, '麻婆豆腐', '四川传统名菜，豆腐嫩滑，麻辣鲜香', 18.00, '/images/dishes/mapo_doufu.jpg', 30, TRUE, 2),
(1, '红烧肉', '传统家常菜，肥瘦相间，香甜软糯', 35.00, '/images/dishes/hongshao_rou.jpg', 25, TRUE, 3),
(1, '糖醋里脊', '酸甜可口，外酥内嫩，老少皆宜', 32.00, '/images/dishes/tangcu_liji.jpg', 40, TRUE, 4),
(1, '鱼香肉丝', '川菜经典，酸甜微辣，下饭神器', 26.00, '/images/dishes/yuxiang_rousi.jpg', 35, TRUE, 5),
(1, '回锅肉', '川菜之王，肥而不腻，香辣下饭', 30.00, '/images/dishes/huiguo_rou.jpg', 20, TRUE, 6),

-- 凉菜
(2, '凉拌黄瓜', '清爽开胃，夏日必备，爽脆可口', 12.00, '/images/dishes/liangban_huanggua.jpg', 50, TRUE, 1),
(2, '口水鸡', '四川名菜，麻辣鲜香，口感丰富', 25.00, '/images/dishes/koushui_ji.jpg', 30, TRUE, 2),
(2, '拍黄瓜', '简单爽口，蒜香浓郁，开胃小菜', 10.00, '/images/dishes/pai_huanggua.jpg', 40, TRUE, 3),
(2, '凉拌木耳', '营养丰富，口感爽脆，健康美味', 15.00, '/images/dishes/liangban_muer.jpg', 35, TRUE, 4),
(2, '蒜泥白肉', '四川传统凉菜，肥而不腻，蒜香浓郁', 28.00, '/images/dishes/suanni_bairou.jpg', 25, TRUE, 5),

-- 汤类
(3, '西红柿鸡蛋汤', '家常汤品，营养丰富，酸甜可口', 15.00, '/images/dishes/xihongshi_jidan_tang.jpg', 60, TRUE, 1),
(3, '冬瓜排骨汤', '清淡营养，滋补养生，老火靓汤', 28.00, '/images/dishes/donggua_paigu_tang.jpg', 30, TRUE, 2),
(3, '紫菜蛋花汤', '清淡鲜美，营养丰富，简单易做', 12.00, '/images/dishes/zicai_danhua_tang.jpg', 50, TRUE, 3),
(3, '酸辣汤', '酸辣开胃，口感丰富，经典汤品', 18.00, '/images/dishes/suanla_tang.jpg', 40, TRUE, 4),
(3, '银耳莲子汤', '滋阴润燥，美容养颜，甜品汤品', 20.00, '/images/dishes/yiner_lianzi_tang.jpg', 25, TRUE, 5),

-- 饮品
(4, '鲜榨橙汁', '新鲜橙子现榨，维C丰富，酸甜可口', 15.00, '/images/drinks/xianzha_chengzhi.jpg', 100, TRUE, 1),
(4, '柠檬蜂蜜茶', '清香怡人，美容养颜，酸甜适中', 18.00, '/images/drinks/ningmeng_fengmi_cha.jpg', 80, TRUE, 2),
(4, '绿豆汤', '清热解毒，消暑降火，夏日佳品', 12.00, '/images/drinks/lvdou_tang.jpg', 60, TRUE, 3),
(4, '酸梅汤', '生津止渴，开胃消食，传统饮品', 14.00, '/images/drinks/suanmei_tang.jpg', 70, TRUE, 4),
(4, '红茶', '香醇浓郁，提神醒脑，经典茶饮', 10.00, '/images/drinks/hong_cha.jpg', 90, TRUE, 5),
(4, '绿茶', '清香淡雅，清热降火，健康茶饮', 10.00, '/images/drinks/lv_cha.jpg', 90, TRUE, 6),
(4, '可乐', '经典碳酸饮料，冰爽解腻', 8.00, '/images/drinks/kele.jpg', 120, TRUE, 7),
(4, '雪碧', '柠檬味汽水，清爽怡人', 8.00, '/images/drinks/xuebi.jpg', 120, TRUE, 8);