package com.vanju.module.analyzer.service.scd;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vanju.framework.common.pojo.PageParam;
import com.vanju.framework.common.pojo.PageResult;
import com.vanju.module.analyzer.controller.scd.params.GetDataGroupByBatchNoParams;
import com.vanju.module.analyzer.controller.scd.params.GetModelDataParams;
import com.vanju.module.analyzer.dal.dataobject.scd.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public interface ScdService {

    PageResult<ScdRegaddrValDO> getDataGroupByBatchNoByScdId(GetDataGroupByBatchNoParams getDataGroupByBatchNoParams);

    void checkAndCreateTable(String tableName);

    String saveScdFile(MultipartFile scdFile, String stationId) throws IOException, ParseException, InterruptedException;

    void parseScdFile(String filePath, Date parse_time,String stationId);

    List<ScdDeviceValueDO> createScdFileData(String filePath, Date date,String type) throws InterruptedException, IOException;

    Page<ScdCurrentInfoDO> selectModelData(String deviceName, String scdContact, String type, Integer PageNo, Integer PageSize);

    List<String> getCheckTime(String stationSign,String type);

    List<ScdCheckFrontDO> getScdCheckFrontData(String stationSign, String option, String time) throws ParseException;

    List<ScdCheckAfterDO> getScdCheckAfterData(String stationSign,String option,String time) throws ParseException;
}
