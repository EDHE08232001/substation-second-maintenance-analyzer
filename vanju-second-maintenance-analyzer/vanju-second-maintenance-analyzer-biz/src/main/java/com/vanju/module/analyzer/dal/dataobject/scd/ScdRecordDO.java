package com.vanju.module.analyzer.dal.dataobject.scd;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("biz_scd_record")
public class ScdRecordDO {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String fileName;

    private String stationId;

    private String filePath;

    private Date uploadTime;
}
