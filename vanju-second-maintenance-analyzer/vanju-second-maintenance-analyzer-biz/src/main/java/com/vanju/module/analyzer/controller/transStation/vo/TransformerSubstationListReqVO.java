package com.vanju.module.analyzer.controller.transStation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Schema(description = "管理后台 - 部门列表 Request VO")
@Data
public class TransformerSubstationListReqVO {

    @Schema(description = "部门名称，模糊匹配", example = "万炬")
    private String name;
    /**
     *
     */
    private Integer scd;
}
