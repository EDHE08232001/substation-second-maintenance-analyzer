package com.vanju.module.analyzer.dal.dataobject.publicHandle;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_scd_constant")
public class ScdConstantDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String description;

    private String reference;

    private int status;

    private String deviceId;

    private String deviceName;

    private String scdSign;

    private String stationSign;

    private String value;

    private Date saveTime;
}
