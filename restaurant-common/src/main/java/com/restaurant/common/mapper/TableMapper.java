package com.restaurant.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.restaurant.common.entity.Table;
import org.apache.ibatis.annotations.Mapper;

/**
 * 桌台Mapper接口
 */
@Mapper
public interface TableMapper extends BaseMapper<Table> {
}