package com.vanju.module.analyzer.dal.mysql.scd;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdRegaddrDO;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 */
@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface ScdRegaddrMapper extends BaseMapperX<ScdRegaddrDO> {
}
