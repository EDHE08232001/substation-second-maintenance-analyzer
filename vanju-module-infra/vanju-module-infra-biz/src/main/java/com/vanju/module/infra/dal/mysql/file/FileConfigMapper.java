package com.vanju.module.infra.dal.mysql.file;

import com.vanju.framework.common.pojo.PageResult;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.infra.controller.file.vo.config.FileConfigPageReqVO;
import com.vanju.module.infra.dal.dataobject.file.FileConfigDO;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileConfigMapper extends BaseMapperX<FileConfigDO> {

    default PageResult<FileConfigDO> selectPage(FileConfigPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<FileConfigDO>()
                .likeIfPresent(FileConfigDO::getName, reqVO.getName())
                .eqIfPresent(FileConfigDO::getStorage, reqVO.getStorage())
                .betweenIfPresent(FileConfigDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(FileConfigDO::getId));
    }

    default FileConfigDO selectByMaster() {
        return selectOne(FileConfigDO::getMaster, true);
    }

}
