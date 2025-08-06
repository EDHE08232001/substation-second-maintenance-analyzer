package com.vanju.module.analyzer.dal.mysql.transStation;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.vanju.framework.mybatis.core.mapper.BaseMapperX;
import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationListReqVO;
import com.vanju.module.analyzer.dal.dataobject.transStation.TransformerSubstationDO;
import com.vanju.module.analyzer.enums.SCDFileEnum;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.List;

/**
 *
 */
@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface TransformerSubstationMapper extends BaseMapperX<TransformerSubstationDO> {

    default List<TransformerSubstationDO> selectList(TransformerSubstationListReqVO reqVO) {
        return selectList(new LambdaQueryWrapperX<TransformerSubstationDO>().likeIfPresent(TransformerSubstationDO::getName, reqVO.getName()).eqIfPresent(TransformerSubstationDO::getScd, reqVO.getScd()));
    }

    default TransformerSubstationDO selectByParentIdAndName(Long parentId, String name) {
        return selectOne(TransformerSubstationDO::getParentId, parentId, TransformerSubstationDO::getName, name);
    }

    default Long selectCountByParentId(Long parentId) {
        return selectCount(TransformerSubstationDO::getParentId, parentId);
    }

    default List<TransformerSubstationDO> selectListByParentId(Collection<Long> parentIds) {
        return selectList(TransformerSubstationDO::getParentId, parentIds);
    }

    //    default List<ScdRegaddrDO> selectListByLeaderUserId(Long id) {
    //        return selectList(ScdRegaddrDO::getLeaderUserId, id);
    //    }
}
