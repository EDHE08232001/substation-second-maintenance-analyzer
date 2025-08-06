package com.vanju.module.analyzer.dal.mysql.scd;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.module.analyzer.dal.dataobject.scd.DeviceDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface DeviceMapper extends BaseMapperX<DeviceDO> {

    default List<DeviceDO> selectListLike(SFunction<DeviceDO, ?> field, String value) {
        return selectList(new LambdaQueryWrapper<DeviceDO>().like(field, value));
    }

}
