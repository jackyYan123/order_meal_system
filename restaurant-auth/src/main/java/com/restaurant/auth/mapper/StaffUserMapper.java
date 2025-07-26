package com.restaurant.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.auth.entity.StaffUser;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Mapper接口（员工）
 */
@Mapper
public interface StaffUserMapper extends BaseMapper<StaffUser> {
}