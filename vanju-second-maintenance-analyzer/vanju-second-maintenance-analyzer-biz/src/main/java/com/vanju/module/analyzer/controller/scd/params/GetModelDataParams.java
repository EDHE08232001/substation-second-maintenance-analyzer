package com.vanju.module.analyzer.controller.scd.params;

import com.vanju.framework.common.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 获取模型数据分页查询参数
 */
@Schema(description = "获取模型数据分页查询参数")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class GetModelDataParams extends PageParam {

    @Schema(description = "装置名称", example = "CL2201")
    private String deviceName;

    @Schema(description = "SCD标识", example = "station-103/2025-6-25_15_51_06")
    private String scdContact;

    @Schema(description = "数据类型", example = "定值", allowableValues = {"定值", "装置参数", "光功率", "压板", "运行信息", "开关量", "模拟量"})
    private String type;
} 