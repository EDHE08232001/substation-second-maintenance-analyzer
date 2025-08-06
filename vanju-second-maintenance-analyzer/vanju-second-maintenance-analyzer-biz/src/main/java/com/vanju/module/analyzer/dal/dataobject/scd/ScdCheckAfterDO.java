package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_scd_dev_check_after")
public class ScdCheckAfterDO {
    private String description;
    private String reference;
    private int type;
    private String value;
    private int status;

    private String deviceName;
    private String deviceId;

    private String scdSign;

    private String stationSign;

    Date checkTime;
    
    // 新增字段：数据集名称
    private String dataSetName;
    
    // 新增字段：数据集引用标识
    private String dataSetSign;
}
