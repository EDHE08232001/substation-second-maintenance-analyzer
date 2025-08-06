package com.vanju.module.analyzer.controller.scd.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class ScdBatchVO {
    /**
     *
     */
    private Long batchNo;
    /**
     *
     */
    private Long scdId;
    /**
     *
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createDatetime;
}
