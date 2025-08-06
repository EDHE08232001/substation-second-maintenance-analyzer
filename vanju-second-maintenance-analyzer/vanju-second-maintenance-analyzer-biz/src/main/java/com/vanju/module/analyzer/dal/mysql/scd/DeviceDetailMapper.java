package com.vanju.module.analyzer.dal.mysql.scd;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.module.analyzer.dal.dataobject.scd.DeviceDetailDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface DeviceDetailMapper extends BaseMapperX<DeviceDetailDO> {

}
