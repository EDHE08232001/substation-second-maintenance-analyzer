package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_scd_dev_current_info")
public class ScdCurrentInfoDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String description;

    private String reference;

    private int type;

    private int status;

    private String deviceName;

    private String deviceId;

    private String scdSign;

    private String stationSign;

    private String value = String.valueOf(-1);

    private String lightIntensity = String.valueOf(10);

    @TableField(exist = false)
    private String YaBanType;

    private Date valueUpdateTime;
    
    // 新增字段：数据集名称
    private String dataSetName;

    private String dataSetSign;
}
