package com.restaurant.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.restaurant.auth.entity.Customer;
import com.restaurant.auth.service.CustomerService;
import com.restaurant.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 顾客控制器
 */
@RestController
@RequestMapping("/customers")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 获取所有顾客
     */
    @GetMapping
    public Result<List<Customer>> getAllCustomers() {
        List<Customer> customers = customerService.list();
        return Result.success(customers);
    }

    /**
     * 分页查询顾客
     */
    @GetMapping("/page")
    public Result<IPage<Customer>> getCustomersByPage(
            @RequestParam(defaultValue = "1") int current,
            @RequestParam(defaultValue = "10") int size) {
        Page<Customer> page = new Page<>(current, size);
        IPage<Customer> customerPage = customerService.page(page);
        return Result.success(customerPage);
    }

    /**
     * 根据ID获取顾客
     */
    @GetMapping("/{id}")
    public Result<Customer> getCustomerById(@PathVariable Long id) {
        Customer customer = customerService.getById(id);
        if (customer == null) {
            return Result.error("CUSTOMER_NOT_FOUND", "顾客不存在");
        }
        return Result.success(customer);
    }

    /**
     * 根据手机号获取顾客
     */
    @GetMapping("/phone/{phone}")
    public Result<Customer> getCustomerByPhone(@PathVariable String phone) {
        Customer customer = customerService.findByPhone(phone);
        if (customer == null) {
            return Result.error("CUSTOMER_NOT_FOUND", "顾客不存在");
        }
        return Result.success(customer);
    }

    /**
     * 根据微信OpenID获取顾客
     */
    @GetMapping("/wechat/{openid}")
    public Result<Customer> getCustomerByWechatOpenid(@PathVariable String openid) {
        Customer customer = customerService.findByWechatOpenid(openid);
        if (customer == null) {
            return Result.error("CUSTOMER_NOT_FOUND", "顾客不存在");
        }
        return Result.success(customer);
    }

    /**
     * 创建顾客
     */
    @PostMapping
    public Result<Customer> createCustomer(@RequestBody Customer customer) {
        boolean saved = customerService.save(customer);
        if (saved) {
            return Result.success(customer);
        }
        return Result.error("CREATE_FAILED", "创建顾客失败");
    }

    /**
     * 创建或获取顾客（用于下单时的顾客信息处理）
     */
    @PostMapping("/create-or-get")
    public Result<Customer> createOrGetCustomer(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String name) {
        Customer customer = customerService.createOrGetCustomer(phone, name);
        return Result.success(customer);
    }

    /**
     * 更新顾客
     */
    @PutMapping("/{id}")
    public Result<Customer> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        customer.setId(id);
        boolean updated = customerService.updateById(customer);
        if (updated) {
            return Result.success(customer);
        }
        return Result.error("UPDATE_FAILED", "更新顾客失败");
    }

    /**
     * 删除顾客（逻辑删除）
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteCustomer(@PathVariable Long id) {
        boolean deleted = customerService.removeById(id);
        if (deleted) {
            return Result.success();
        }
        return Result.error("DELETE_FAILED", "删除顾客失败");
    }
}