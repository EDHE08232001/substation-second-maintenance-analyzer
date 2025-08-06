package com.vanju.module.analyzer.service.transStation.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import com.google.common.annotations.VisibleForTesting;
import com.vanju.framework.common.exception.ErrorCode;
import com.vanju.framework.common.util.object.BeanUtils;
import com.vanju.framework.datapermission.core.annotation.DataPermission;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationListReqVO;
import com.vanju.module.analyzer.controller.transStation.vo.TransformerSubstationSaveReqVO;
import com.vanju.module.analyzer.dal.dataobject.scd.DeviceDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentModelDO;
import com.vanju.module.analyzer.dal.dataobject.transStation.TransformerSubstationDO;
import com.vanju.module.analyzer.dal.mysql.scd.DeviceMapper;
import com.vanju.module.analyzer.dal.mysql.scd.ScdCurrentModelMapper;
import com.vanju.module.analyzer.dal.mysql.transStation.TransformerSubstationMapper;
import com.vanju.module.analyzer.enums.SCDFileEnum;
import com.vanju.module.analyzer.service.transStation.TransformerSubstationService;
import com.vanju.module.infra.api.file.FileApi;
import com.vanju.module.system.dal.redis.RedisKeyConstants;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

import static com.vanju.framework.common.exception.util.ServiceExceptionUtil.exception;
import static com.vanju.framework.common.util.collection.CollectionUtils.convertSet;

/**
 *
 */
@Service
@Slf4j
public class TransformerSubstationServiceImpl implements TransformerSubstationService {

    /**
     *
     */
    // @Value("${vanju.scd-file-path}")
    private String scdFilePath;

    @Value("${scd.upload.path}")
    private String publicScdFilePath;

    @Resource
    private TransformerSubstationMapper transformerSubstationMapper;
    @Autowired
    DeviceMapper deviceMapper;
    @Autowired
    ScdCurrentModelMapper scdCurrentModelMapper;

    @Resource
    FileApi fileApi;

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.TRANS_STATION_CHILDREN_ID_LIST, allEntries = true)
    // allEntries 清空所有缓存，因为操作一个站，涉及到多个缓存
    public Long createTransformerSubstation(TransformerSubstationSaveReqVO createReqVO) {
        if (createReqVO.getParentId() == null) {
            createReqVO.setParentId(TransformerSubstationDO.PARENT_ID_ROOT);
        }
        // 校验父站的有效性
        validateParentTransformerSubstation(null, createReqVO.getParentId());
        // 校验站名的唯一性
        validateTransformerSubstationNameUnique(null, createReqVO.getParentId(), createReqVO.getName());

        // 插入站
        TransformerSubstationDO TransformerSubstation = BeanUtils.toBean(createReqVO, TransformerSubstationDO.class);
        transformerSubstationMapper.insert(TransformerSubstation);
        if (SCDFileEnum.TRUE.getIsSCDFile() == createReqVO.getScd() && !createReqVO.getScdFile().isEmpty()) {
            try {
                fileApi.createFile(createReqVO.getName(), scdFilePath + UUID.randomUUID(), IoUtil.readBytes(createReqVO.getScdFile().getInputStream()));
            } catch (IOException e) {
                log.error("upload scd file error : {}", e);
                throw new RuntimeException(e);
            }
        }
        return TransformerSubstation.getId();
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.TRANS_STATION_CHILDREN_ID_LIST, allEntries = true)
    // allEntries 清空所有缓存，因为操作一个站，涉及到多个缓存
    public void updateTransformerSubstation(TransformerSubstationSaveReqVO updateReqVO) {
        if (updateReqVO.getParentId() == null) {
            updateReqVO.setParentId(TransformerSubstationDO.PARENT_ID_ROOT);
        }
        // 校验自己存在
        validateTransformerSubstationExists(updateReqVO.getId());
        // 校验父站的有效性
        validateParentTransformerSubstation(updateReqVO.getId(), updateReqVO.getParentId());
        // 校验站名的唯一性
        validateTransformerSubstationNameUnique(updateReqVO.getId(), updateReqVO.getParentId(), updateReqVO.getName());

        // 更新站
        TransformerSubstationDO updateObj = BeanUtils.toBean(updateReqVO, TransformerSubstationDO.class);
        transformerSubstationMapper.updateById(updateObj);
    }

    @Override
    @CacheEvict(cacheNames = RedisKeyConstants.TRANS_STATION_CHILDREN_ID_LIST, allEntries = true)
    // allEntries 清空所有缓存，因为操作一个站，涉及到多个缓存
    public void deleteTransformerSubstation(Long id) {
        // 校验是否存在
        validateTransformerSubstationExists(id);
        // 校验是否有子站
        if (transformerSubstationMapper.selectCountByParentId(id) > 0) {
            throw exception(new ErrorCode(-1, "存在子站无法删除"));
        }
        // 删除站
        transformerSubstationMapper.deleteById(id);
    }

    @VisibleForTesting
    void validateTransformerSubstationExists(Long id) {
        if (id == null) {
            return;
        }
        TransformerSubstationDO TransformerSubstation = transformerSubstationMapper.selectById(id);
        if (TransformerSubstation == null) {
            throw exception(new ErrorCode(-1, "当前站不存在"));
        }
    }

    @VisibleForTesting
    void validateParentTransformerSubstation(Long id, Long parentId) {
        if (parentId == null || TransformerSubstationDO.PARENT_ID_ROOT.equals(parentId)) {
            return;
        }
        // 1. 不能设置自己为父站
        if (Objects.equals(id, parentId)) {
            throw exception(new ErrorCode(-1, "不能设置自己为父站"));
        }
        // 2. 父站不存在
        TransformerSubstationDO parentTransformerSubstation = transformerSubstationMapper.selectById(parentId);
        if (parentTransformerSubstation == null) {
            throw exception(new ErrorCode(-1, "父级站不存在"));
        }
        // 3. 递归校验父站，如果父站是自己的子站，则报错，避免形成环路
        if (id == null) { // id 为空，说明新增，不需要考虑环路
            return;
        }
        for (int i = 0; i < Short.MAX_VALUE; i++) {
            // 3.1 校验环路
            parentId = parentTransformerSubstation.getParentId();
            if (Objects.equals(id, parentId)) {
                throw exception(new ErrorCode(-1, "不能设置自己的子站为父站"));
            }
            // 3.2 继续递归下一级父站
            if (parentId == null || TransformerSubstationDO.PARENT_ID_ROOT.equals(parentId)) {
                break;
            }
            parentTransformerSubstation = transformerSubstationMapper.selectById(parentId);
            if (parentTransformerSubstation == null) {
                break;
            }
        }
    }

    @VisibleForTesting
    void validateTransformerSubstationNameUnique(Long id, Long parentId, String name) {
        TransformerSubstationDO TransformerSubstation = transformerSubstationMapper.selectByParentIdAndName(parentId, name);
        if (TransformerSubstation == null) {
            return;
        }
        // 如果 id 为空，说明不用比较是否为相同 id 的站
        if (id == null) {
            throw exception(new ErrorCode(-1, "已经存在该名字的站"));
        }
        if (ObjectUtil.notEqual(TransformerSubstation.getId(), id)) {
            throw exception(new ErrorCode(-1, "已经存在该名字的站"));
        }
    }

    @Override
    public TransformerSubstationDO getTransformerSubstation(Long id) {
        return transformerSubstationMapper.selectById(id);
    }

    @Override
    public List<TransformerSubstationDO> getTransformerSubstationList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return transformerSubstationMapper.selectBatchIds(ids);
    }

    @Override
    public List<TransformerSubstationDO> getTransformerSubstationList(TransformerSubstationListReqVO reqVO) {
        List<TransformerSubstationDO> list = transformerSubstationMapper.selectList(reqVO);
        list.sort(Comparator.comparing(TransformerSubstationDO::getSort));
        return list;
    }

    @Override
    public List<TransformerSubstationDO> getChildTransformerSubstationList(Collection<Long> ids) {
        List<TransformerSubstationDO> children = new LinkedList<>();
        // 遍历每一层
        Collection<Long> parentIds = ids;
        for (int i = 0; i < Short.MAX_VALUE; i++) { // 使用 Short.MAX_VALUE 避免 bug 场景下，存在死循环
            // 查询当前层，所有的子站
            List<TransformerSubstationDO> TransformerSubstations = transformerSubstationMapper.selectListByParentId(parentIds);
            // 1. 如果没有子站，则结束遍历
            if (CollUtil.isEmpty(TransformerSubstations)) {
                break;
            }
            // 2. 如果有子站，继续遍历
            children.addAll(TransformerSubstations);
            parentIds = convertSet(TransformerSubstations, TransformerSubstationDO::getId);
        }
        return children;
    }

    @Override
    @DataPermission(enable = false) // 禁用数据权限，避免建立不正确的缓存
    @Cacheable(cacheNames = RedisKeyConstants.TRANS_STATION_CHILDREN_ID_LIST, key = "#id")
    public Set<Long> getChildTransformerSubstationIdListFromCache(Long id) {
        List<TransformerSubstationDO> children = getChildTransformerSubstationList(id);
        return convertSet(children, TransformerSubstationDO::getId);
    }

    @Override
    public void validateTransformerSubstationList(Collection<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        // 获得科室信息
        Map<Long, TransformerSubstationDO> TransformerSubstationMap = getTransformerSubstationMap(ids);
        // 校验
        ids.forEach(id -> {
            TransformerSubstationDO TransformerSubstation = TransformerSubstationMap.get(id);
            if (TransformerSubstation == null) {
                throw exception(new ErrorCode(-1, "当前站不存在"));
            }
                    //    if (!CommonStatusEnum.ENABLE.getStatus().equals(TransformerSubstation.getStatus())) {
                    //        throw exception(new ErrorCode(-1, "站({})不处于开启状态，不允许选择"), TransformerSubstation.getName());
                    //    }
        });
    }

    @Override
    public List<ScdCurrentModelDO> selectDevListData(String station_id){
        List<ScdCurrentModelDO> deviceDOList = scdCurrentModelMapper.selectListLike(ScdCurrentModelDO::getScdContact,station_id);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for(ScdCurrentModelDO dev:deviceDOList){
            dev.setParseTime_String(sdf.format(dev.getParseTime()));
            dev.setScdContact_final(publicScdFilePath + File.separator + dev.getScdContact());
        }
        return deviceDOList;
    }



}
