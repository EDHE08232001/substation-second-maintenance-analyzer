package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ScdDeviceValueDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String deviceName;   // 装置名称
    private String deviceId;    // 装置标识 (如CL2201)
    private String ipAddress;   // IP地址
    private int port;           // 端口号
    private int devStatus;
    private Date parseTime;
    private String scdContact;

    @TableField(exist = false)
    private List<ScdValueDO> details = new ArrayList<>(); // 装置明细列表
}
