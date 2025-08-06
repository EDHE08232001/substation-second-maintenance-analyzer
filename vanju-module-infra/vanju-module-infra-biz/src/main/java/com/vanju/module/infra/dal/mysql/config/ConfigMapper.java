package com.vanju.module.infra.dal.mysql.config;

import com.vanju.framework.common.pojo.PageResult;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.infra.controller.config.vo.ConfigPageReqVO;
import com.vanju.module.infra.dal.dataobject.config.ConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ConfigMapper extends BaseMapperX<ConfigDO> {

    default ConfigDO selectByKey(String key) {
        return selectOne(ConfigDO::getConfigKey, key);
    }

    default PageResult<ConfigDO> selectPage(ConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<ConfigDO>()
                .likeIfPresent(ConfigDO::getName, reqVO.getName())
                .likeIfPresent(ConfigDO::getConfigKey, reqVO.getKey())
                .eqIfPresent(ConfigDO::getType, reqVO.getType())
                .betweenIfPresent(ConfigDO::getCreateTime, reqVO.getCreateTime()));
    }

}
