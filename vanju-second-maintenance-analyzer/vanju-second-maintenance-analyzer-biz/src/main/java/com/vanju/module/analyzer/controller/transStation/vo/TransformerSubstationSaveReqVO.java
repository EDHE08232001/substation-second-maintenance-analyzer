package com.vanju.module.analyzer.controller.transStation.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Schema(description = "管理后台 - 变电站创建/修改 Request VO")
@Data
public class TransformerSubstationSaveReqVO {

    @Schema(description = "变电站编号", example = "1024")
    private Long id;

    @Schema(description = "变电站名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "万炬")
    @NotBlank(message = "变电站名称不能为空")
    @Size(max = 30, message = "变电站名称长度不能超过 30 个字符")
    private String name;

    @Schema(description = "父变电站 ID", example = "1024")
    private Long parentId;

    @Schema(description = "显示顺序", requiredMode = Schema.RequiredMode.REQUIRED, example = "1024")
    @NotNull(message = "显示顺序不能为空")
    private Integer sort;

    /**
     * 是否scd
     */
    private Integer scd;
    /**
     * scd文件
     */
    MultipartFile scdFile;
}
