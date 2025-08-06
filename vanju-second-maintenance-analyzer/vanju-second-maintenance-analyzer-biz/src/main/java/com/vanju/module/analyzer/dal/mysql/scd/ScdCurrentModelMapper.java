package com.vanju.module.analyzer.dal.mysql.scd;


import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.module.analyzer.dal.dataobject.scd.DeviceDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentModelDO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface ScdCurrentModelMapper extends BaseMapperX<ScdCurrentModelDO> {
    default List<ScdCurrentModelDO> selectListLike(SFunction<ScdCurrentModelDO, ?> field, String value) {
        return selectList(new LambdaQueryWrapper<ScdCurrentModelDO>().like(field, value));
    }
}
