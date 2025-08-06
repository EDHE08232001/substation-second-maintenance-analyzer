package com.vanju.module.analyzer.controller.transStation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "管理后台 - 变电站精简信息 Response VO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransformerSubstationSimpleRespVO {

    @Schema(description = "变电站编号", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long id;

    @Schema(description = "变电站名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "万炬")
    private String name;

    @Schema(description = "父变电站 ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    private Long parentId;

}
