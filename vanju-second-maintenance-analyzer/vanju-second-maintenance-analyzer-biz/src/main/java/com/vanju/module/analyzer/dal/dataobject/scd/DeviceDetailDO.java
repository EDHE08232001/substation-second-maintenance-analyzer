package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("biz_scd_dev_info")
public class DeviceDetailDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String description;
    private String reference;
    private int type;
//    private String value;
    private int status;

    private String deviceName;
    private String deviceId;

    private String scdSign;
    private String stationSign;
    
    // 新增字段：数据集名称
    private String dataSetName;
    
    // 新增字段：数据集引用标识
    private String dataSetSign;

}