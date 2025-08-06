package com.vanju.module.analyzer.dal.mysql.scd;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.vanju.framework.common.pojo.PageResult;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.analyzer.controller.scd.params.GetDataGroupByBatchNoParams;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdRegaddrValDO;
import org.apache.ibatis.annotations.Mapper;

/**
 *
 */
@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface ScdRegaddrValMapper extends BaseMapperX<ScdRegaddrValDO> {
    /**
     * @param getDataGroupByBatchNoParams
     * @return
     */
    default PageResult<ScdRegaddrValDO> getPageGroupByBatchNoByScdId(GetDataGroupByBatchNoParams getDataGroupByBatchNoParams) {
        LambdaQueryWrapperX<ScdRegaddrValDO> queryWrapper = new LambdaQueryWrapperX();
        queryWrapper.eq(ScdRegaddrValDO::getScdId, getDataGroupByBatchNoParams.getScdId());
        queryWrapper.groupBy(ScdRegaddrValDO::getBatchNo);
        return selectPage(getDataGroupByBatchNoParams, queryWrapper);
    }
}
