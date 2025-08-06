package com.vanju.module.infra.dal.mysql.db;

import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.module.infra.dal.dataobject.db.DataSourceConfigDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据源配置 Mapper
 *
 * @author 万炬源码
 */
@Mapper
public interface DataSourceConfigMapper extends BaseMapperX<DataSourceConfigDO> {
}
