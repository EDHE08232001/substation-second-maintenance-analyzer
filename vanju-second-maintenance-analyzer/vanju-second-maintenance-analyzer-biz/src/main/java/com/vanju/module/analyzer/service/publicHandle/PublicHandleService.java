package com.vanju.module.analyzer.service.publicHandle;

import com.vanju.module.analyzer.dal.dataobject.publicHandle.ScdConstantDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentInfoDO;
import com.vanju.module.analyzer.dal.dataobject.scd.ScdCurrentModelDO;

import java.util.List;

public interface PublicHandleService {

    String saveConstantData(List<ScdCurrentInfoDO> objects);

    List<ScdConstantDO> selectConstantData(String stationSign);

}
