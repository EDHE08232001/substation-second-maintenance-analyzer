package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_scd_regaddr_val")
public class ScdRegaddrValDO {
    //    private Long id;
    //    private String name;
    //    private String regAddr;
    //    private Long transformerSubstationId;
    //    private String type;
    //    private Date createDatetime;
    //    private Date updateDatetime;

    private Long id;
    private Long batchNo;
    private Long scdId;
    private String regAddr;
    private String regAddrVal;
    private Date createDatetime;
}
