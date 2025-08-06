package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_scd_regaddr")
public class ScdRegaddrDO {
    private Long id;
    private String name;
    private String regAddr;
    private Long scdId;
    private String type;
    private Date createDatetime;
    private Date updateDatetime;
}
