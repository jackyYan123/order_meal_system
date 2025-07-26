package com.restaurant.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.restaurant.auth.entity.Customer;
import com.restaurant.auth.mapper.CustomerMapper;
import com.restaurant.auth.service.CustomerService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 顾客服务实现类
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public Customer findByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        return this.getOne(queryWrapper);
    }

    @Override
    public Customer findByWechatOpenid(String wechatOpenid) {
        if (wechatOpenid == null || wechatOpenid.trim().isEmpty()) {
            return null;
        }
        QueryWrapper<Customer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("wechat_openid", wechatOpenid);
        return this.getOne(queryWrapper);
    }

    @Override
    public Customer createOrGetCustomer(String phone, String name) {
        // 如果提供了手机号，先尝试查找现有顾客
        if (phone != null && !phone.trim().isEmpty()) {
            Customer existingCustomer = findByPhone(phone);
            if (existingCustomer != null) {
                // 如果提供了姓名且现有顾客没有姓名，则更新姓名
                if (name != null && !name.trim().isEmpty() && 
                    (existingCustomer.getName() == null || existingCustomer.getName().trim().isEmpty())) {
                    existingCustomer.setName(name);
                    this.updateById(existingCustomer);
                }
                return existingCustomer;
            }
        }

        // 创建新顾客
        Customer newCustomer = new Customer();
        newCustomer.setPhone(phone);
        newCustomer.setName(name);
        newCustomer.setMemberLevel("NORMAL");
        newCustomer.setTotalOrders(0);
        newCustomer.setTotalAmount(BigDecimal.ZERO);
        
        this.save(newCustomer);
        return newCustomer;
    }

    @Override
    public void updateCustomerStats(Long customerId, int orderIncrement, BigDecimal amountIncrement) {
        if (customerId == null) {
            return;
        }
        
        Customer customer = this.getById(customerId);
        if (customer != null) {
            customer.setTotalOrders(customer.getTotalOrders() + orderIncrement);
            customer.setTotalAmount(customer.getTotalAmount().add(amountIncrement));
            
            // 根据消费金额自动升级会员等级
            BigDecimal totalAmount = customer.getTotalAmount();
            if (totalAmount.compareTo(new BigDecimal("10000")) >= 0) {
                customer.setMemberLevel("GOLD");
            } else if (totalAmount.compareTo(new BigDecimal("5000")) >= 0) {
                customer.setMemberLevel("VIP");
            }
            
            this.updateById(customer);
        }
    }
}