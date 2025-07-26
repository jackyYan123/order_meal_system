package com.restaurant.auth.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.restaurant.auth.entity.Customer;

/**
 * 顾客服务接口
 */
public interface CustomerService extends IService<Customer> {

    /**
     * 根据手机号查找顾客
     */
    Customer findByPhone(String phone);

    /**
     * 根据微信OpenID查找顾客
     */
    Customer findByWechatOpenid(String wechatOpenid);

    /**
     * 创建或获取顾客（用于匿名下单后的顾客信息补全）
     */
    Customer createOrGetCustomer(String phone, String name);

    /**
     * 更新顾客统计信息（订单数和消费金额）
     */
    void updateCustomerStats(Long customerId, int orderIncrement, java.math.BigDecimal amountIncrement);
}