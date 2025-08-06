package com.vanju.module.analyzer.service.publicHandle.impl;

import com.vanju.framework.mybatis.core.query.LambdaQueryWrapperX;
import com.vanju.module.analyzer.dal.dataobject.publicHandle.ScdConstantDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentInfoDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentModelDO;
import com.vanju.module.analyzer.dal.mysql.publicHandle.ScdConstantMapper;
import com.vanju.module.analyzer.service.publicHandle.PublicHandleService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class PublicHandleServiceImpl implements PublicHandleService {

    @Resource
    ScdConstantMapper scdConstantMapper;


    @Override
    public String saveConstantData(List<ScdCurrentInfoDO> objects){

        String stationSign = null;
        List<ScdConstantDO> scdConstantDOS = new ArrayList<ScdConstantDO>();
        if (objects != null || objects.size() != 0){
            stationSign = objects.get(0).getStationSign();
            scdConstantDOS = scdConstantMapper.selectList(
                    new LambdaQueryWrapperX<ScdConstantDO>()
                            .eq(ScdConstantDO::getStationSign, stationSign)
            );
        }
        else {
            return null;
        }
        if (scdConstantDOS != null && scdConstantDOS.size() != 0){
            scdConstantMapper.delete(ScdConstantDO::getStationSign, stationSign);
            scdConstantMapper.delete(ScdConstantDO::getStationSign, stationSign);
        }
        Date saveTime = new Date();
        List<ScdConstantDO> scdConstantDOList = new ArrayList<>();
        for (ScdCurrentInfoDO scdCurrentInfoDO : objects){
            ScdConstantDO scdConstantDO = new ScdConstantDO();
            scdConstantDO.setReference(scdCurrentInfoDO.getReference());
            scdConstantDO.setDescription(scdCurrentInfoDO.getDescription());
            scdConstantDO.setStatus(scdCurrentInfoDO.getStatus());
            scdConstantDO.setDeviceId(scdCurrentInfoDO.getDeviceId());
            scdConstantDO.setDeviceName(scdCurrentInfoDO.getDeviceName());
            scdConstantDO.setScdSign(scdCurrentInfoDO.getScdSign());
            scdConstantDO.setStationSign(scdCurrentInfoDO.getStationSign());
            scdConstantDO.setValue(scdCurrentInfoDO.getValue());
            scdConstantDO.setSaveTime(saveTime);

            scdConstantDOList.add(scdConstantDO);
        }
        scdConstantMapper.insert(scdConstantDOList);

        return "设定成功";
    }

    @Override
    public List<ScdConstantDO> selectConstantData(String scdSign){

        List<ScdConstantDO> scdConstantDOS = scdConstantMapper.selectList(
                new LambdaQueryWrapperX<ScdConstantDO>()
                        .eq(ScdConstantDO::getScdSign, scdSign)
        );

        return scdConstantDOS;
    }


}
