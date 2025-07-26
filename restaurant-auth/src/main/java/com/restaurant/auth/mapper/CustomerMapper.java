package com.restaurant.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.auth.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

/**
 * 顾客Mapper接口
 */
@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}