package com.vanju.module.analyzer.controller.scd.vo;

import lombok.Data;

import java.util.Date;

/**
 *
 */
@Data
public class ScdRegAddrValDiffVO {
    private String name1;
    private String name2;
    private String value1;
    private String value2;
    private Date date1;
    private Date date2;
}
