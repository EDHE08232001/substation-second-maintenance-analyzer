package com.vanju.module.analyzer.controller.transStation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Schema(description = "管理后台 - 变电站信息 Response VO")
@Data
public class TransformerSubstationRespVO {

    @Schema(description = "变电站编号", example = "1024")
    private Long id;

    @Schema(description = "变电站名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "万炬")
    private String name;

    @Schema(description = "父变电站 ID", example = "1024")
    private Long parentId;

    @Schema(description = "变电站ID", example = "1024")
    private String stationId;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Integer sort;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

}
