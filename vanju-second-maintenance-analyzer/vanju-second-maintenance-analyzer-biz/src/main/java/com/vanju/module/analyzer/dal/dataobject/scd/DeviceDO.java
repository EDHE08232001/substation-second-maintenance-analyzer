package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Transient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@TableName("biz_scd_dev_model")
public class DeviceDO {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String deviceName;   // 装置名称
    private String deviceId;    // 装置标识 (如CL2201)
    private String ipAddress;   // IP地址
    private int port;           // 端口号
    private int devStatus;
    private Date parseTime;
    private String scdContact;
    private String stationSign;

    @TableField(exist = false)
    private List<DeviceDetailDO> details = new ArrayList<>(); // 装置明细列表

    @TableField(exist = false)
    private String parseTime_String;

    @TableField(exist = false)
    private String scdContact_final;
}