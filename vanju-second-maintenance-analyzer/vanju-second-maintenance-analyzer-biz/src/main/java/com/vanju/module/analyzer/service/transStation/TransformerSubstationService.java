package com.vanju.module.analyzer.service.transStation;

import com.vanju.framework.common.util.collection.CollectionUtils;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationListReqVO;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationSaveReqVO;
import com.vanju.module.analyzer.dal.dataobject.scd.DeviceDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentModelDO;
import com.vanju.module.analyzer.dal.dataobject.transStation.TransformerSubstationDO;

import java.util.*;

public interface TransformerSubstationService {

    /**
     * 创建部门
     *
     * @param createReqVO 部门信息
     * @return 部门编号
     */
    Long createTransformerSubstation(TransformerSubstationSaveReqVO createReqVO);

    /**
     * 更新部门
     *
     * @param updateReqVO 部门信息
     */
    void updateTransformerSubstation(TransformerSubstationSaveReqVO updateReqVO);

    /**
     * 删除部门
     *
     * @param id 部门编号
     */
    void deleteTransformerSubstation(Long id);

    /**
     * 获得部门信息
     *
     * @param id 部门编号
     * @return 部门信息
     */
    TransformerSubstationDO getTransformerSubstation(Long id);

    /**
     * 获得部门信息数组
     *
     * @param ids 部门编号数组
     * @return 部门信息数组
     */
    List<TransformerSubstationDO> getTransformerSubstationList(Collection<Long> ids);

    /**
     * 筛选部门列表
     *
     * @param reqVO 筛选条件请求 VO
     * @return 部门列表
     */
    List<TransformerSubstationDO> getTransformerSubstationList(TransformerSubstationListReqVO reqVO);

    /**
     * 获得指定编号的部门 Map
     *
     * @param ids 部门编号数组
     * @return 部门 Map
     */
    default Map<Long, TransformerSubstationDO> getTransformerSubstationMap(Collection<Long> ids) {
        List<TransformerSubstationDO> list = getTransformerSubstationList(ids);
        return CollectionUtils.convertMap(list, TransformerSubstationDO::getId);
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param id 部门编号
     * @return 子部门列表
     */
    default List<TransformerSubstationDO> getChildTransformerSubstationList(Long id) {
        return getChildTransformerSubstationList(Collections.singleton(id));
    }

    /**
     * 获得指定部门的所有子部门
     *
     * @param ids 部门编号数组
     * @return 子部门列表
     */
    List<TransformerSubstationDO> getChildTransformerSubstationList(Collection<Long> ids);

    /**
     * 获得所有子部门，从缓存中
     *
     * @param id 父部门编号
     * @return 子部门列表
     */
    Set<Long> getChildTransformerSubstationIdListFromCache(Long id);

    /**
     * 校验部门们是否有效。如下情况，视为无效：
     * 1. 部门编号不存在
     * 2. 部门被禁用
     *
     * @param ids 角色编号数组
     */
    void validateTransformerSubstationList(Collection<Long> ids);

    List<ScdCurrentModelDO> selectDevListData(String station_id);
}
